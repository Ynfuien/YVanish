**Project is still WIP, though everything should work fine.**

[![Discord](https://img.shields.io/discord/1236019317208776786?style=flat&logo=discord&label=Discord&color=%235d6af2
)](https://discord.gg/kZJhKZ48j8)
# YVanish
And at last, a vanish plugin, **the** vanish plugin! You may ask, "Wh**Y** yet another vanish?" (Hence the "Y" in the name ;p). And I come with the answer. It's because every other plugin (*every* meaning [3 that I tested](https://github.com/Ynfuien/YVanish/wiki/6.-Plugin-comparison)), let through a few packets that revealed vanished players. Also, I wanted silent chests done correctly, not with the spectator "trick".

Supports **Paper** 1.20 - 1.20.4.<br/>
For full functionality, depends on [ProtocolLib](https://github.com/dmulloy2/ProtocolLib/) and its [dev builds](https://ci.dmulloy2.net/job/ProtocolLib/).<br/>
1.20.6 support when ProtocolLib updates.

# Features
### Vanish (well, duh)
- on join
- silent join and quit messages
- invisible in a tab list (also kinda obvious)
- hidden in server status (player count, player sample)

### Silent chests
Opened chests (normal, trapped, ender), shulker boxes and barrels work just like vanish. You can see when normal players do it, but they can't see you do it. Accomplished with virgin tears, sweat of a troll and a unicorn horn. Ah and also pinch of ProtocolLib dark magic.

### Cultural mobs
An option to stop mobs from randomly staring at a player. What's good of being invisible if all mobs coincidentally look all in one direction? Exactly, not much.<br/>
*Doesn't work with mobs using a different AI system like villagers, piglins, hoglins and some more.

### Other
- silent sculk sensors
- silent advancement and death messages
- disabled item and exp pickup
- hidden from monsters targeting
- action and boss bar for custom messages
- Fully customizable messages with a lang file, supporting [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) and [MiniMessage](https://docs.advntr.dev/minimessage/index.html)
- Commands with tab completions
- [API](https://github.com/Ynfuien/YVanish/wiki/4.-Developer-API) for developers

## Options
Almost every option can be changed per player with a `/vanishoptions` (`/vo`) command. Allowing for use of appropriate features in appropriate situations. Command and subcommands have associated permissions so customizability shouldn't be a problem.

# Download
You can download the plugin only on [Modrinth](https://modrinth.com/plugin/yvanish) or compile it yourself.

# Documentation
You can read about plugin's [permissions](https://github.com/Ynfuien/YVanish/wiki/2.-Permissions), [placeholders](https://github.com/Ynfuien/YVanish/wiki/3.-Placeholders) etc. on the [wiki](https://github.com/Ynfuien/YVanish/wiki) page.

# Media
Vanish options<br/>
![vanish options](https://i.imgur.com/5NfhYlv.gif)

Silent chests<br/>
![silent chests](https://i.imgur.com/UcsLPJX.gif)

# License
This project uses [GNU GPLv3](https://github.com/Ynfuien/YVanish/main/blob/LICENSE) license.