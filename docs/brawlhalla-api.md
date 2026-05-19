# Brawlhalla API Documentation

> **Mise à jour :** Mai 2026 — Générée depuis des appels réels à `https://api.brawlhalla.com`.
> La [documentation officielle](https://dev.brawlhalla.com/#introduction) n'est plus à jour.

---

## Sommaire

- [Base URL & Authentification](#base-url--authentification)
- [Limites de taux](#limites-de-taux)
- [Endpoints](#endpoints)
  - [Recherche par Steam ID](#recherche-par-steam-id)
  - [Classements (Rankings)](#classements-rankings)
  - [Statistiques d'un joueur](#statistiques-dun-joueur)
  - [Classement ranked d'un joueur](#classement-ranked-dun-joueur)
  - [Clans](#clans)
  - [Tous les Legends](#tous-les-legends)
  - [Un Legend spécifique](#un-legend-spécifique)
- [Codes d'erreur](#codes-derreur)
- [Référence des valeurs](#référence-des-valeurs)
  - [Régions](#régions)
  - [Brackets](#brackets)
  - [Tiers (rangs)](#tiers-rangs)
  - [Armes](#armes)
  - [Liste des Legends](#liste-des-legends)

---

## Base URL & Authentification

```
Base URL : https://api.brawlhalla.com
```

Toutes les requêtes doivent inclure la clé API en query parameter :

```
?api_key={VOTRE_CLE}
```

**Exemple :**

```
GET https://api.brawlhalla.com/legend/all?api_key=XXXXXXXXXXXXXXXX
```

> L'API requiert obligatoirement HTTPS. Une requête HTTP retournera une erreur 401.

---

## Limites de taux

| Fenêtre       | Limite     |
|---------------|------------|
| 15 minutes    | 180 appels |
| Par seconde   | 10 appels  |

Un dépassement retourne une erreur `429`.

---

## Endpoints

### Recherche par Steam ID

Permet de récupérer l'identifiant Brawlhalla d'un joueur à partir de son Steam ID 64.

```
GET /search?steamid={steamID64}&api_key={KEY}
```

**Paramètres query :**

| Paramètre | Type   | Requis | Description                |
|-----------|--------|--------|----------------------------|
| `steamid` | string | Oui    | Steam ID au format 64 bits |
| `api_key` | string | Oui    | Clé API                    |

**Réponse :**

```json
{
  "brawlhalla_id": 2,
  "name": "bmgtherealfirstdan"
}
```

| Champ            | Type    | Description                   |
|------------------|---------|-------------------------------|
| `brawlhalla_id`  | integer | Identifiant Brawlhalla         |
| `name`           | string  | Pseudo du joueur               |

---

### Classements (Rankings)

Retourne les 50 joueurs/équipes d'une page de classement.

```
GET /rankings/{bracket}/{region}/{page}?api_key={KEY}
```

**Paramètres URL :**

| Paramètre | Type    | Valeurs possibles                                         |
|-----------|---------|-----------------------------------------------------------|
| `bracket` | string  | `1v1`, `2v2`, `rotating` (voir [Brackets](#brackets))    |
| `region`  | string  | `all`, `us-e`, `eu`, `sea`, `brz`, `aus`, `us-w`, `jpn`, `sa` |
| `page`    | integer | Commence à `1`                                            |

**Paramètres query supplémentaires :**

| Paramètre | Type   | Requis | Description                                       |
|-----------|--------|--------|---------------------------------------------------|
| `name`    | string | Non    | Filtre par nom (1v1 uniquement). Correspondance partielle possible. |

---

#### Réponse — bracket `1v1`

```json
[
  {
    "rank": 1,
    "name": "Btk bag",
    "brawlhalla_id": 83834655,
    "best_legend": 35,
    "best_legend_games": 429,
    "best_legend_wins": 412,
    "rating": 2837,
    "tier": "Valhallan",
    "games": 429,
    "wins": 412,
    "region": "US-E",
    "peak_rating": 2837
  }
]
```

| Champ               | Type    | Description                              |
|---------------------|---------|------------------------------------------|
| `rank`              | integer | Position dans le classement              |
| `name`              | string  | Pseudo du joueur                         |
| `brawlhalla_id`     | integer | Identifiant Brawlhalla                   |
| `best_legend`       | integer | ID du legend le plus joué                |
| `best_legend_games` | integer | Parties jouées avec ce legend            |
| `best_legend_wins`  | integer | Victoires avec ce legend                 |
| `rating`            | integer | ELO actuel                               |
| `tier`              | string  | Tier actuel (ex: `Valhallan`, `Diamond`) |
| `games`             | integer | Nombre total de parties                  |
| `wins`              | integer | Nombre total de victoires                |
| `region`            | string  | Région du joueur                         |
| `peak_rating`       | integer | ELO maximum atteint                      |

---

#### Réponse — bracket `2v2`

```json
[
  {
    "rank": 1,
    "teamname": "Zen+Qewdiy",
    "brawlhalla_id_one": 5827766,
    "brawlhalla_id_two": 12620130,
    "rating": 2826,
    "tier": "Valhallan",
    "wins": 503,
    "games": 620,
    "region": "US-E",
    "peak_rating": 2835
  }
]
```

| Champ              | Type    | Description                          |
|--------------------|---------|--------------------------------------|
| `rank`             | integer | Position dans le classement          |
| `teamname`         | string  | Nom de l'équipe                      |
| `brawlhalla_id_one`| integer | ID Brawlhalla du joueur 1            |
| `brawlhalla_id_two`| integer | ID Brawlhalla du joueur 2            |
| `rating`           | integer | ELO actuel de l'équipe               |
| `tier`             | string  | Tier actuel                          |
| `wins`             | integer | Victoires de l'équipe                |
| `games`            | integer | Parties jouées par l'équipe          |
| `region`           | string  | Région de l'équipe                   |
| `peak_rating`      | integer | ELO maximum atteint                  |

---

#### Réponse — bracket `rotating`

```json
[
  {
    "rank": 1,
    "name": "bh",
    "brawlhalla_id": 4189861,
    "rating": 2188,
    "tier": "Diamond",
    "games": 277,
    "wins": 243,
    "region": "US-E",
    "peak_rating": 2213
  }
]
```

| Champ         | Type    | Description                    |
|---------------|---------|--------------------------------|
| `rank`        | integer | Position dans le classement    |
| `name`        | string  | Pseudo du joueur               |
| `brawlhalla_id`| integer | Identifiant Brawlhalla        |
| `rating`      | integer | ELO actuel                     |
| `tier`        | string  | Tier actuel                    |
| `games`       | integer | Nombre de parties              |
| `wins`        | integer | Nombre de victoires            |
| `region`      | string  | Région du joueur               |
| `peak_rating` | integer | ELO maximum atteint            |

---

### Statistiques d'un joueur

Retourne les statistiques globales et par legend d'un joueur.

```
GET /player/{brawlhalla_id}/stats?api_key={KEY}
```

**Paramètres URL :**

| Paramètre       | Type    | Description            |
|-----------------|---------|------------------------|
| `brawlhalla_id` | integer | Identifiant Brawlhalla |

**Réponse :**

```json
{
  "brawlhalla_id": 2,
  "name": "bmgtherealfirstdan",
  "xp": 481914,
  "level": 70,
  "xp_percentage": 0.596,
  "games": 2585,
  "wins": 1255,
  "damagebomb": "2708",
  "damagemine": "1691",
  "damagespikeball": "846",
  "damagesidekick": "2465",
  "hitsnowball": 318,
  "kobomb": 16,
  "komine": 16,
  "kospikeball": 6,
  "kosidekick": 3,
  "kosnowball": 76,
  "legends": [...]
}
```

**Champs du joueur :**

| Champ            | Type    | Description                                      |
|------------------|---------|--------------------------------------------------|
| `brawlhalla_id`  | integer | Identifiant Brawlhalla                            |
| `name`           | string  | Pseudo                                           |
| `xp`             | integer | XP total                                         |
| `level`          | integer | Niveau du compte                                 |
| `xp_percentage`  | float   | Progression vers le niveau suivant (0.0 à 1.0)   |
| `games`          | integer | Nombre total de parties                          |
| `wins`           | integer | Nombre total de victoires                        |
| `damagebomb`     | string  | Dégâts infligés avec les bombes                  |
| `damagemine`     | string  | Dégâts infligés avec les mines                   |
| `damagespikeball`| string  | Dégâts infligés avec les spike balls             |
| `damagesidekick` | string  | Dégâts infligés avec les sidekicks               |
| `hitsnowball`    | integer | Nombre de boules de neige touchées               |
| `kobomb`         | integer | KOs avec les bombes                              |
| `komine`         | integer | KOs avec les mines                               |
| `kospikeball`    | integer | KOs avec les spike balls                         |
| `kosidekick`     | integer | KOs avec les sidekicks                           |
| `kosnowball`     | integer | KOs avec les boules de neige                     |
| `legends`        | array   | Statistiques détaillées par legend (voir ci-dessous) |

**Champs d'un legend dans `legends` :**

| Champ               | Type    | Description                                      |
|---------------------|---------|--------------------------------------------------|
| `legend_id`         | integer | ID du legend                                     |
| `legend_name_key`   | string  | Clé du nom du legend                             |
| `damagedealt`       | string  | Dégâts totaux infligés                           |
| `damagetaken`       | string  | Dégâts totaux reçus                             |
| `kos`               | integer | KOs effectués                                    |
| `falls`             | integer | Fois où le joueur est tombé                      |
| `suicides`          | integer | Suicides                                         |
| `teamkos`           | integer | KOs alliés                                       |
| `matchtime`         | integer | Temps en jeu (en secondes)                       |
| `games`             | integer | Parties jouées avec ce legend                    |
| `wins`              | integer | Victoires avec ce legend                         |
| `damageunarmed`     | string  | Dégâts à mains nues                             |
| `damagethrownitem`  | string  | Dégâts avec objets jetés                         |
| `damageweaponone`   | string  | Dégâts avec l'arme principale                    |
| `damageweapontwo`   | string  | Dégâts avec l'arme secondaire                    |
| `damagegadgets`     | string  | Dégâts avec gadgets                              |
| `kounarmed`         | integer | KOs à mains nues                                 |
| `kothrownitem`      | integer | KOs avec objets jetés                            |
| `koweaponone`       | integer | KOs avec l'arme principale                       |
| `koweapontwo`       | integer | KOs avec l'arme secondaire                       |
| `kogadgets`         | integer | KOs avec gadgets                                 |
| `timeheldweaponone` | integer | Temps de possession de l'arme principale (sec)   |
| `timeheldweapontwo` | integer | Temps de possession de l'arme secondaire (sec)   |
| `xp`                | integer | XP gagné avec ce legend                          |
| `level`             | integer | Niveau du legend                                 |
| `xp_percentage`     | float   | Progression vers le niveau suivant (0.0 à 1.0)   |

---

### Classement ranked d'un joueur

Retourne le classement ranked 1v1, 2v2 et rotating d'un joueur.

```
GET /player/{brawlhalla_id}/ranked?api_key={KEY}
```

**Paramètres URL :**

| Paramètre       | Type    | Description            |
|-----------------|---------|------------------------|
| `brawlhalla_id` | integer | Identifiant Brawlhalla |

**Réponse :**

```json
{
  "name": "Btk bag",
  "brawlhalla_id": 83834655,
  "rating": 2837,
  "peak_rating": 2837,
  "tier": "Valhallan",
  "wins": 412,
  "games": 429,
  "region": "US-E",
  "global_rank": 1,
  "region_rank": 1,
  "legends": [...],
  "2v2": [...],
  "rotating_ranked": {...}
}
```

**Champs de la réponse :**

| Champ             | Type    | Description                                        |
|-------------------|---------|----------------------------------------------------|
| `name`            | string  | Pseudo du joueur                                   |
| `brawlhalla_id`   | integer | Identifiant Brawlhalla                              |
| `rating`          | integer | ELO 1v1 actuel                                     |
| `peak_rating`     | integer | ELO 1v1 maximum atteint                            |
| `tier`            | string  | Tier 1v1 actuel                                    |
| `wins`            | integer | Victoires en 1v1 ranked                            |
| `games`           | integer | Parties jouées en 1v1 ranked                       |
| `region`          | string  | Région principale du joueur                        |
| `global_rank`     | integer | Rang mondial en 1v1 (0 si non classé)              |
| `region_rank`     | integer | Rang régional en 1v1 (0 si non classé)             |
| `legends`         | array   | Statistiques ranked par legend (voir ci-dessous)   |
| `2v2`             | array   | Équipes 2v2 et leurs stats (voir ci-dessous)       |
| `rotating_ranked` | object  | Stats en rotating ranked (voir ci-dessous)         |

**Champs d'un legend dans `legends` :**

| Champ             | Type    | Description                         |
|-------------------|---------|-------------------------------------|
| `legend_id`       | integer | ID du legend                        |
| `legend_name_key` | string  | Clé du nom du legend                |
| `rating`          | integer | ELO avec ce legend                  |
| `peak_rating`     | integer | ELO maximum avec ce legend          |
| `tier`            | string  | Tier avec ce legend                 |
| `wins`            | integer | Victoires avec ce legend            |
| `games`           | integer | Parties avec ce legend              |

**Champs d'une équipe dans `2v2` :**

| Champ              | Type    | Description                        |
|--------------------|---------|------------------------------------|
| `brawlhalla_id_one`| integer | ID du premier joueur               |
| `brawlhalla_id_two`| integer | ID du deuxième joueur              |
| `rating`           | integer | ELO de l'équipe                    |
| `peak_rating`      | integer | ELO maximum de l'équipe            |
| `tier`             | string  | Tier de l'équipe                   |
| `wins`             | integer | Victoires de l'équipe              |
| `games`            | integer | Parties de l'équipe                |
| `teamname`         | string  | Nom de l'équipe                    |
| `region`           | integer | Code numérique de la région        |
| `global_rank`      | integer | Rang mondial (0 si non classé)     |

**Champs de `rotating_ranked` :**

| Champ         | Type    | Description                         |
|---------------|---------|-------------------------------------|
| `name`        | string  | Pseudo du joueur                    |
| `brawlhalla_id`| integer| Identifiant Brawlhalla              |
| `rating`      | integer | ELO en rotating                     |
| `peak_rating` | integer | ELO maximum en rotating             |
| `tier`        | string  | Tier en rotating                    |
| `wins`        | integer | Victoires en rotating               |
| `games`       | integer | Parties en rotating                 |
| `region`      | string  | Région                              |

---

### Clans

Retourne les informations d'un clan et la liste de ses membres.

```
GET /clan/{clan_id}?api_key={KEY}
```

**Paramètres URL :**

| Paramètre | Type    | Description  |
|-----------|---------|--------------|
| `clan_id` | integer | ID du clan   |

**Réponse :**

```json
{
  "clan_id": 1,
  "clan_name": "Blue Mammoth Games",
  "clan_create_date": 1464206400,
  "clan_xp": "165103",
  "clan_lifetime_xp": 3876384,
  "clan": [
    {
      "brawlhalla_id": 3,
      "name": "Chill Penguin X",
      "rank": "Leader",
      "join_date": 1464206400,
      "xp": 235958
    }
  ]
}
```

**Champs du clan :**

| Champ              | Type    | Description                                     |
|--------------------|---------|-------------------------------------------------|
| `clan_id`          | integer | Identifiant du clan                             |
| `clan_name`        | string  | Nom du clan                                     |
| `clan_create_date` | integer | Date de création (timestamp Unix)               |
| `clan_xp`          | string  | XP actuel du clan                               |
| `clan_lifetime_xp` | integer | XP total accumulé depuis la création            |
| `clan`             | array   | Liste des membres                               |

**Champs d'un membre dans `clan` :**

| Champ           | Type    | Description                                          |
|-----------------|---------|------------------------------------------------------|
| `brawlhalla_id` | integer | Identifiant Brawlhalla du membre                     |
| `name`          | string  | Pseudo du membre                                     |
| `rank`          | string  | Rang dans le clan (`Leader`, `Officer`, `Member`)    |
| `join_date`     | integer | Date d'adhésion (timestamp Unix)                     |
| `xp`            | integer | XP apporté par le membre au clan                     |

---

### Tous les Legends

Retourne la liste de tous les legends avec leurs statistiques de base.

```
GET /legend/all?api_key={KEY}
```

**Réponse :** Tableau d'objets legend.

```json
[
  {
    "legend_id": 3,
    "legend_name_key": "bodvar",
    "bio_name": "Bödvar",
    "bio_aka": "The Unconquered Viking, The Great Bear",
    "weapon_one": "Hammer",
    "weapon_two": "Sword",
    "strength": "6",
    "dexterity": "6",
    "defense": "5",
    "speed": "5"
  }
]
```

| Champ             | Type   | Description                                  |
|-------------------|--------|----------------------------------------------|
| `legend_id`       | integer| ID du legend                                 |
| `legend_name_key` | string | Clé interne du nom                           |
| `bio_name`        | string | Nom complet du legend                        |
| `bio_aka`         | string | Surnom(s)                                    |
| `weapon_one`      | string | Arme principale                              |
| `weapon_two`      | string | Arme secondaire                              |
| `strength`        | string | Statistique Force (1–10)                     |
| `dexterity`       | string | Statistique Dextérité (1–10)                 |
| `defense`         | string | Statistique Défense (1–10)                   |
| `speed`           | string | Statistique Vitesse (1–10)                   |

> La somme des 4 statistiques est toujours égale à 22.

---

### Un Legend spécifique

Retourne les informations complètes d'un legend, y compris sa biographie.

```
GET /legend/{legend_id}?api_key={KEY}
```

**Paramètres URL :**

| Paramètre   | Type    | Description   |
|-------------|---------|---------------|
| `legend_id` | integer | ID du legend  |

**Réponse :**

```json
{
  "legend_id": 3,
  "legend_name_key": "bodvar",
  "bio_name": "Bödvar",
  "bio_aka": "The Unconquered Viking, The Great Bear",
  "bio_quote": "\"I speak, you noble vikings, of a warrior who surpassed you all.\"",
  "bio_quote_about_attrib": "\"-The Saga of Bödvar Bearson, first stanza\"",
  "bio_quote_from": "\"Listen you nine-mothered bridge troll, I'm coming in.\"",
  "bio_quote_from_attrib": "\"-Bödvar to Heimdall, guardian of the gates of Asgard\"",
  "bio_text": "Born of a viking mother and bear father...",
  "bot_name": "Bötvar",
  "weapon_one": "Hammer",
  "weapon_two": "Sword",
  "strength": "6",
  "dexterity": "6",
  "defense": "5",
  "speed": "5"
}
```

| Champ                    | Type   | Description                                         |
|--------------------------|--------|-----------------------------------------------------|
| `legend_id`              | integer| ID du legend                                        |
| `legend_name_key`        | string | Clé interne du nom                                  |
| `bio_name`               | string | Nom complet du legend                               |
| `bio_aka`                | string | Surnom(s)                                           |
| `bio_quote`              | string | Citation à propos du legend                         |
| `bio_quote_about_attrib` | string | Attribution de la citation sur le legend            |
| `bio_quote_from`         | string | Citation prononcée par le legend                    |
| `bio_quote_from_attrib`  | string | Attribution de la citation du legend                |
| `bio_text`               | string | Biographie complète                                 |
| `bot_name`               | string | Nom du bot IA correspondant à ce legend             |
| `weapon_one`             | string | Arme principale                                     |
| `weapon_two`             | string | Arme secondaire                                     |
| `strength`               | string | Statistique Force (1–10)                            |
| `dexterity`              | string | Statistique Dextérité (1–10)                        |
| `defense`                | string | Statistique Défense (1–10)                          |
| `speed`                  | string | Statistique Vitesse (1–10)                          |

---

## Codes d'erreur

| Code HTTP | Description                                                       |
|-----------|-------------------------------------------------------------------|
| `401`     | HTTPS requis — la requête a été faite en HTTP                    |
| `403`     | Clé API invalide ou absente                                       |
| `404`     | Endpoint introuvable ou paramètre invalide                        |
| `429`     | Limite de taux dépassée                                           |
| `503`     | Service temporairement indisponible                               |

**Format de la réponse d'erreur :**

```json
{
  "error": {
    "code": 404,
    "message": "Bad Request"
  }
}
```

---

## Référence des valeurs

### Régions

| Valeur  | Description         |
|---------|---------------------|
| `all`   | Toutes les régions  |
| `us-e`  | États-Unis Est      |
| `us-w`  | États-Unis Ouest    |
| `eu`    | Europe              |
| `sea`   | Asie du Sud-Est     |
| `brz`   | Brésil              |
| `aus`   | Australie           |
| `jpn`   | Japon               |
| `sa`    | Amérique du Sud     |

> `jpn` et `sa` sont des régions actives non documentées officiellement.

---

### Brackets

| Valeur     | Description                              |
|------------|------------------------------------------|
| `1v1`      | Solo ranked                              |
| `2v2`      | Duo ranked                               |
| `rotating` | Ranked en mode rotatif (ex: Kung Foot)   |

> Le bracket `kungfoot` mentionné dans l'ancienne documentation ne fonctionne plus (retourne 404). Utiliser `rotating` à la place.

---

### Tiers (rangs)

Les tiers sont listés du plus bas au plus haut :

| Tier        | ELO approximatif |
|-------------|-----------------|
| Tin 0       | < 726           |
| Tin 1–5     | 726 – 874       |
| Bronze 1–5  | 875 – 1124      |
| Silver 1–5  | 1125 – 1374     |
| Gold 1–5    | 1375 – 1624     |
| Platinum 1–5| 1625 – 1874     |
| Diamond 1–5 | 1875 – 2199     |
| Valhallan   | ≥ 2200          |

---

### Armes

Liste complète des armes présentes dans le jeu (au 14/05/2026) :

| Arme          |
|---------------|
| Axe           |
| Bow           |
| Cannon        |
| Chakram       |
| Fists         |
| Greatsword    |
| Hammer        |
| Katar         |
| Orb           |
| Pistol        |
| RocketLance   |
| Scythe        |
| Spear         |
| Sword         |
| Boots         |

---

### Liste des Legends

68 legends disponibles au 14/05/2026 :

| ID | Nom           | Arme 1       | Arme 2       |
|----|---------------|--------------|--------------|
| 3  | Bödvar        | Hammer       | Sword        |
| 4  | Cassidy       | Pistol       | Hammer       |
| 5  | Orion         | RocketLance  | Spear        |
| 6  | Lord Vraxx    | RocketLance  | Pistol       |
| 7  | Gnash         | Hammer       | Spear        |
| 8  | Queen Nai     | Spear        | Katar        |
| 9  | Lucien        | Katar        | Pistol       |
| 10 | Hattori       | Sword        | Spear        |
| 11 | Sir Roland    | RocketLance  | Sword        |
| 12 | Scarlet       | Hammer       | RocketLance  |
| 13 | Thatch        | Sword        | Pistol       |
| 14 | Ada           | Pistol       | Spear        |
| 15 | Sentinel      | Hammer       | Katar        |
| 16 | Teros         | Axe          | Hammer       |
| 17 | Red Raptor    | Boots        | Orb          |
| 18 | Ember         | Bow          | Katar        |
| 19 | Brynn         | Axe          | Spear        |
| 20 | Asuri         | Katar        | Sword        |
| 21 | Barraza       | Axe          | Pistol       |
| 22 | Ulgrim        | Axe          | RocketLance  |
| 23 | Azoth         | Bow          | Axe          |
| 24 | Koji          | Bow          | Sword        |
| 25 | Diana         | Bow          | Pistol       |
| 26 | Jhala         | Axe          | Sword        |
| 27 | Loki          | Katar        | Scythe       |
| 28 | Kor           | Fists        | Hammer       |
| 29 | Wu Shang      | Fists        | Spear        |
| 30 | Val           | Fists        | Sword        |
| 31 | Ragnir        | Katar        | Axe          |
| 32 | Cross         | Pistol       | Fists        |
| 33 | Mirage        | Scythe       | Spear        |
| 34 | Nix           | Scythe       | Pistol       |
| 35 | Mordex        | Scythe       | Fists        |
| 36 | Yumiko        | Bow          | Hammer       |
| 37 | Artemis       | RocketLance  | Scythe       |
| 38 | Caspian       | Fists        | Katar        |
| 39 | Sidra         | Cannon       | Sword        |
| 40 | Xull          | Cannon       | Axe          |
| 41 | Isaiah        | Cannon       | Pistol       |
| 42 | Kaya          | Spear        | Bow          |
| 43 | Jiro          | Sword        | Scythe       |
| 44 | Lin Fei       | Katar        | Cannon       |
| 45 | Zariel        | Fists        | Bow          |
| 46 | Rayman        | Fists        | Axe          |
| 47 | Dusk          | Spear        | Orb          |
| 48 | Fait          | Scythe       | Orb          |
| 49 | Thor          | Hammer       | Orb          |
| 50 | Petra         | Fists        | Orb          |
| 51 | Vector        | RocketLance  | Bow          |
| 52 | Volkov        | Axe          | Scythe       |
| 53 | Onyx          | Fists        | Cannon       |
| 54 | Jaeyun        | Sword        | Greatsword   |
| 55 | Mako          | Katar        | Greatsword   |
| 56 | Magyar        | Hammer       | Greatsword   |
| 57 | Reno          | Pistol       | Orb          |
| 58 | Munin         | Bow          | Scythe       |
| 59 | Arcadia       | Spear        | Greatsword   |
| 60 | Ezio          | Sword        | Orb          |
| 61 | Seven         | Spear        | Cannon       |
| 62 | Thea          | Boots        | RocketLance  |
| 63 | Tezca         | Boots        | Fists        |
| 64 | Vivi          | Boots        | Pistol       |
| 65 | Imugi         | Axe          | Greatsword   |
| 66 | King Zuva     | Hammer       | Boots        |
| 67 | Priya         | Chakram      | Sword        |
| 68 | Ransom        | Chakram      | Bow          |
| 69 | Lady Vera     | Chakram      | Scythe       |
| 70 | Rupture       | Katar        | RocketLance  |
