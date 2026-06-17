import asyncio
import logging
import os
import signal

import discord
from discord.ext import commands
from dotenv import load_dotenv

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

load_dotenv()

COGS = ["cogs.player", "cogs.stats"]


async def main() -> None:
    token = os.getenv("DISCORD_TOKEN")
    if not token:
        raise ValueError("DISCORD_TOKEN is not set in environment variables.")

    intents = discord.Intents.default()
    bot = commands.Bot(command_prefix="/", intents=intents)

    @bot.event
    async def on_ready() -> None:
        logger.info("Logged in as %s (ID: %s)", bot.user, bot.user.id)
        synced = await bot.tree.sync()
        logger.info("Synced %d slash command(s)", len(synced))

    @bot.tree.error
    async def on_app_command_error(
        interaction: discord.Interaction,
        error: discord.app_commands.AppCommandError,
    ) -> None:
        logger.exception("Unhandled slash command error", exc_info=error)
        msg = "❌ An unexpected error occurred."
        if interaction.response.is_done():
            await interaction.followup.send(msg, ephemeral=True)
        else:
            await interaction.response.send_message(msg, ephemeral=True)

    async with bot:
        for cog in COGS:
            await bot.load_extension(cog)
            logger.info("Loaded cog: %s", cog)

        loop = asyncio.get_event_loop()
        for sig in (signal.SIGINT, signal.SIGTERM):
            loop.add_signal_handler(sig, lambda: asyncio.create_task(bot.close()))

        await bot.start(token)


if __name__ == "__main__":
    asyncio.run(main())
