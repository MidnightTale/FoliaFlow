# FoliaFlow
![sss1](https://user-images.githubusercontent.com/125941391/231678608-9762d384-dc3a-440c-a4eb-3340cb39a3e3.png)

FoliaFlow is a Minecraft plugin that allows players to using gravity block duplication glitch again in folia (paper soon)

## How is work?
Whenever a falling sand entity comes close to or hits the end portal, a new falling sand entity is summoned with a powerful velocity that shoots it straight into the portal. However, we encountered a problem when the entity got reset by the safe teleport paper patch - it lost its velocity. To fix this, we added the velocity back when the entity was in the end. Another issue we faced was that the entity would instantly turn into a block upon teleportation. To solve this, we made the center block of the spawn platform a slab, ensuring that the entity stays in its falling state. It's fascinating how we can solve complex problems like these


## Installation

To install FoliaFlow, follow these steps:

1. Download the plugin JAR file from the [releases page](https://github.com/Hynse/FoliaFlow/releases).
2. Copy the JAR file to the `plugins` folder of your Minecraft server.
3. Restart your server to enable the plugin.

## Contributing

If you would like to contribute to FoliaFlow, feel free to submit a pull request with your changes. All contributions are welcome and appreciated.

## License

This plugin is licensed under the MIT license. See [LICENSE.md](https://github.com/Hynse/FoliaFlow/blob/master/LICENSE.md) for more information.
