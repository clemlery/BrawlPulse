import os
import logging

import discord
from discord.ext import commands
from dotenv import load_dotenv


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

load_dotenv()


def main() -> None:
    token = os.getenv("DISCORD_TOKEN")
    print(f'========= token : {token} =========')
    if not token:
        raise ValueError("DISCORD_TOKEN is not set in environment variables.")

    intents = discord.Intents.default()
    bot = commands.Bot(command_prefix="!", intents=intents)

    @bot.event
    async def on_ready() -> None:
        logger.info("Logged in as %s (ID: %s)", bot.user, bot.user.id)

    bot.run(token)


if __name__ == "__main__":
    main()