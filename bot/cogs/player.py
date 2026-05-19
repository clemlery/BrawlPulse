import discord
from discord import app_commands
from discord.ext import commands

from services.api_client import ApiClient


class Player(commands.Cog):
    def __init__(self, bot: commands.Bot) -> None:
        self.bot = bot
        self.api = ApiClient()

    @app_commands.command(name="add", description="Start tracking a Brawlhalla player")
    @app_commands.describe(steam_id="Steam ID of the player (17-digit number)")
    async def add(self, interaction: discord.Interaction, steam_id: str) -> None:
        if not steam_id.isdigit() or len(steam_id) != 17:
            await interaction.response.send_message(
                "❌ Invalid Steam ID. It must be a 17-digit number.",
                ephemeral=True,
            )
            return

        await interaction.response.defer()
        result = await self.api.add_player(int(steam_id))

        if result["success"]:
            data = result["data"]
            already_tracked = result["status"] == 200
            embed = discord.Embed(
                title="ℹ️ Already tracked" if already_tracked else "✅ Player added",
                description=(
                    f"**{data['currentName']}** is already being tracked."
                    if already_tracked
                    else f"**{data['currentName']}** is now being tracked."
                ),
                color=discord.Color.blue() if already_tracked else discord.Color.green(),
            )
            embed.add_field(name="Steam ID", value=steam_id, inline=True)
            embed.add_field(name="Brawlhalla ID", value=str(data["brawlhallaId"]), inline=True)
            await interaction.followup.send(embed=embed)
        elif result["error"] == "not_found":
            await interaction.followup.send(
                "❌ This Steam ID is not linked to a Brawlhalla account.",
                ephemeral=True,
            )
        elif result["error"] == "unavailable":
            await interaction.followup.send(
                "⚠️ Brawlhalla API is currently unavailable. Please try again later.",
                ephemeral=True,
            )
        else:
            await interaction.followup.send(
                "❌ An unexpected error occurred. Please try again.",
                ephemeral=True,
            )

    @app_commands.command(name="remove", description="Stop tracking a Brawlhalla player")
    @app_commands.describe(steam_id="Steam ID of the player to remove")
    async def remove(self, interaction: discord.Interaction, steam_id: str) -> None:
        if not steam_id.isdigit():
            await interaction.response.send_message(
                "❌ Invalid Steam ID format.",
                ephemeral=True,
            )
            return

        await interaction.response.defer()
        result = await self.api.remove_player(int(steam_id))

        if result["success"]:
            embed = discord.Embed(
                title="✅ Player removed",
                description=f"Player `{steam_id}` is no longer being tracked.",
                color=discord.Color.orange(),
            )
            await interaction.followup.send(embed=embed)
        elif result["error"] == "not_found":
            await interaction.followup.send(
                f"ℹ️ Player `{steam_id}` is not currently tracked.",
                ephemeral=True,
            )
        else:
            await interaction.followup.send(
                "❌ An unexpected error occurred. Please try again.",
                ephemeral=True,
            )


async def setup(bot: commands.Bot) -> None:
    await bot.add_cog(Player(bot))
