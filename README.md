# CatEssentials

CatEssentials is a modern EssentialsX replacement Minecraft plugin for Paper 1.21+.  
It provides essential commands and features for your server, with a focus on configurability and simplicity.

## Features

- `/catessentials` — Shows plugin version and authors
- `/hub` — Teleport to a hub world, transfer to another server, or use proxy (configurable)
- `/gmc`, `/gms`, `/gma`, `/gmsp` — Quick gamemode switching
- Welcome message on player join (configurable)
- AuthMeReloaded support: prioritize AuthMe messages if installed
- Highly configurable via `config.yml`

## Configuration

All settings are in `plugins/CatEssentials/config.yml`.  
Example:
```yaml
show-welcome-message: true
prefer-authme-messages: true
welcome-message: "Welcome to CatEssentials!"
enable-hub-command: true
hub-mode: "world" # options: "proxy", "transfer", "world"
hub-server: "lobby"
hub-world: "hub"
hub-coords: [0, 100, 0]
```

## Commands

| Command         | Description                                      |
|-----------------|--------------------------------------------------|
| `/catessentials`| Shows plugin version and authors                 |
| `/hub`          | Teleport to hub or transfer to another server    |
| `/gmc`          | Switch to Creative mode                          |
| `/gms`          | Switch to Survival mode                          |
| `/gma`          | Switch to Adventure mode                         |
| `/gmsp`         | Switch to Spectator mode                         |

## Permissions

All commands are available to operators by default.  
Luckperms support will be added soon!

## Requirements

- [PaperMC 1.21+](https://papermc.io/)
- (Optional) [Vault](https://www.spigotmc.org/resources/vault.34315/) and [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for extended features
- (Optional) [AuthMeReloaded](https://www.spigotmc.org/resources/authmereloaded.6269/) for authentication integration

## Contributing

Pull requests and suggestions are welcome!
