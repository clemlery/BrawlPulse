import os

import aiohttp

API_URL = os.getenv("API_URL", "http://localhost:4000")


class ApiClient:
    def __init__(self) -> None:
        self.base_url = API_URL

    async def add_player(self, steam_id: int) -> dict:
        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{self.base_url}/players",
                json={"steamId": steam_id},
            ) as resp:
                if resp.status in (200, 201):
                    return {"success": True, "status": resp.status, "data": await resp.json()}
                elif resp.status == 404:
                    return {"success": False, "error": "not_found", "message": await resp.text()}
                elif resp.status == 503:
                    return {"success": False, "error": "unavailable", "message": await resp.text()}
                return {"success": False, "error": "unknown", "message": await resp.text()}

    async def remove_player(self, steam_id: int) -> dict:
        async with aiohttp.ClientSession() as session:
            async with session.delete(f"{self.base_url}/players/{steam_id}") as resp:
                if resp.status == 204:
                    return {"success": True}
                elif resp.status == 404:
                    return {"success": False, "error": "not_found"}
                return {"success": False, "error": "unknown", "message": await resp.text()}

    async def get_player_stats(self, steam_id: int, period: str = "7d") -> dict:
        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{self.base_url}/players/{steam_id}/stats",
                params={"period": period},
            ) as resp:
                if resp.status == 200:
                    return {"success": True, "data": await resp.json()}
                elif resp.status == 204:
                    return {"success": False, "error": "no_snapshots"}
                elif resp.status == 404:
                    return {"success": False, "error": "not_found"}
                return {"success": False, "error": "unknown", "message": await resp.text()}
