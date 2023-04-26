# FoliaFlow
![sss1](https://user-images.githubusercontent.com/125941391/231678608-9762d384-dc3a-440c-a4eb-3340cb39a3e3.png)

FoliaFlow is a Minecraft plugin that allows players to using gravity block duplication glitch again in Paper and Folia

## How is work?
Whenever a falling sand entity comes close to or hits the end portal, a new falling sand entity is summoned with a velocity that shoots it straight into the portal. However, we encountered a problem when the entity got reset by the safe teleport Paper patch - it lose its velocity. To fix this, we added the velocity back when the entity was in the end.


## Installation

To install FoliaFlow, follow these steps:

1. Download the plugin JAR file from the [Modrinth](https://modrinth.com/plugin/foliaflow).
2. Copy the JAR file to the `plugins` folder of your Minecraft server.
3. Restart your server to load the plugin.

## Config flies (config.yml)
```yml
# Don't touch this file unless you know what you're doing
# Default values seems vanilla.
# If you find values that seems more vanilla please create a PR
# https://github.com/Hynse/FoliaFlow

# Optimize value for horizontal_coefficient
#  - Folia (1.499)
#  - Paper (1.3)
horizontal_coefficient: 1.499
vertical_coefficient: -1.7
spawn_height: 0.08
folia_offset: 0.5
```

## Permissions
`foliaflow.reload` for using command `/flowreload` to reload config flies

## Contributing

If you would like to contribute to FoliaFlow, feel free to submit a pull request with your changes. All contributions are welcome and appreciated.

## License

This plugin is licensed under the MIT license. See [LICENSE.md](https://github.com/Hynse/FoliaFlow/blob/master/LICENSE.md) for more information.
