import discord
from discord import app_commands
from discord.ext import commands

from services.api_client import ApiClient

PERIOD_LABELS = {
    "7d": "last 7 days",
    "4w": "last 4 weeks",
    "6m": "last 6 months",
    "1y": "last year",
    "all": "all time",
}

PERIOD_DAYS = {"7d": 7, "4w": 28, "6m": 180, "1y": 365}


def _build_stats_embed(data: dict, period: str) -> discord.Embed:
    period_label = PERIOD_LABELS.get(period, period)
    wins = data["wins_delta"]
    games = data["games_delta"]
    rating = data["rating_delta"]
    effective = data["effective_days"]
    period_days = PERIOD_DAYS.get(period)
    coverage = f"{effective} / {period_days} days" if period_days else f"{effective} days"

    description = (
        f"```\n"
        f"{'─' * 33}\n"
        f"{'Wins':<14}{'+' if wins >= 0 else ''}{wins}\n"
        f"{'Games':<14}{'+' if games >= 0 else ''}{games}\n"
        f"{'Win rate':<14}{data['winrate']} %\n"
        f"{'Rating':<14}{'+' if rating >= 0 else ''}{rating}  (now {data['current_rating']})\n"
        f"{'─' * 33}\n"
        f"Data covers {coverage}\n"
        f"```"
    )
    return discord.Embed(
        title=f"🎮  {data['name']} — {period_label}",
        description=description,
        color=discord.Color.blurple(),
    )


class PeriodSelect(discord.ui.Select):
    def __init__(self, api: ApiClient, steam_id: int) -> None:
        self.api = api
        self.steam_id = steam_id
        options = [
            discord.SelectOption(label="Last 7 days", value="7d", emoji="📅"),
            discord.SelectOption(label="Last 4 weeks", value="4w", emoji="📆"),
            discord.SelectOption(label="Last 6 months", value="6m", emoji="🗓️"),
            discord.SelectOption(label="Last year", value="1y", emoji="📊"),
            discord.SelectOption(label="All time", value="all", emoji="♾️"),
        ]
        super().__init__(placeholder="Change period…", options=options)

    async def callback(self, interaction: discord.Interaction) -> None:
        period = self.values[0]
        result = await self.api.get_player_stats(self.steam_id, period)
        if result["success"]:
            embed = _build_stats_embed(result["data"], period)
            await interaction.response.edit_message(embed=embed, view=self.view)
        else:
            await interaction.response.send_message(
                "❌ Could not refresh stats. Please try again.",
                ephemeral=True,
            )


class PeriodView(discord.ui.View):
    def __init__(self, api: ApiClient, steam_id: int) -> None:
        super().__init__(timeout=300)
        self.add_item(PeriodSelect(api, steam_id))


class Stats(commands.Cog):
    def __init__(self, bot: commands.Bot) -> None:
        self.bot = bot
        self.api = ApiClient()

    @app_commands.command(name="stats", description="Show stat deltas for a tracked player")
    @app_commands.describe(
        steam_id="Steam ID of the player",
        period="Time period (default: last 7 days)",
    )
    @app_commands.choices(
        period=[
            app_commands.Choice(name="Last 7 days", value="7d"),
            app_commands.Choice(name="Last 4 weeks", value="4w"),
            app_commands.Choice(name="Last 6 months", value="6m"),
            app_commands.Choice(name="Last year", value="1y"),
            app_commands.Choice(name="All time", value="all"),
        ]
    )
    async def stats(
        self,
        interaction: discord.Interaction,
        steam_id: str,
        period: str = "7d",
    ) -> None:
        if not steam_id.isdigit():
            await interaction.response.send_message(
                "❌ Invalid Steam ID format.",
                ephemeral=True,
            )
            return

        await interaction.response.defer()
        result = await self.api.get_player_stats(int(steam_id), period)

        if result["success"]:
            embed = _build_stats_embed(result["data"], period)
            view = PeriodView(self.api, int(steam_id))
            await interaction.followup.send(embed=embed, view=view)
        elif result["error"] == "not_found":
            await interaction.followup.send(
                "❌ This player is not tracked. Use `/add` to start tracking them.",
                ephemeral=True,
            )
        elif result["error"] == "no_snapshots":
            await interaction.followup.send(
                "⏳ No snapshots yet. Stats will be available after the first daily run.",
                ephemeral=True,
            )
        else:
            await interaction.followup.send(
                "❌ An unexpected error occurred. Please try again.",
                ephemeral=True,
            )


async def setup(bot: commands.Bot) -> None:
    await bot.add_cog(Stats(bot))
