<h1 align="center">
  FakeMCServer
  <br>
  <a href="https://github.com/alkanife/alkabot/blob/main/pom.xml">
    <img src="https://img.shields.io/badge/Open%20JDK-17-green" alt="JDK 17">
  </a>
  <a href="https://github.com/alkanife/FakeMCServer/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/alkanife-mc/FakeMCServer" alt="LICENSE">
  </a>
  <a href="https://github.com/alkanife/FakeMCServer/">
    <img src="https://img.shields.io/badge/version-1.2-blue" alt="version">
  </a>
</h1>

<p align="center">
  <b><a href="#overview">Overview</a></b>
  •
  <a href="#examples">Examples</a>
  •
  <a href="#usage">Usage</a>
  •
  <a href="#configuration">Configuration</a>
  •
  <a href="#project-dependencies">Project dependencies</a>
  •
  <a href="#license">License</a>
</p>

## Overview
A fake Minecraft server featuring customizable:
- Server list icon
- MOTD
- Protocol & version name
- Players (with sample)
- Kick message

## Examples

![Example1](https://share.alkanife.fr/github/fakemcserver/1.png)
![Example2](https://share.alkanife.fr/github/fakemcserver/2.png)
![Example3](https://share.alkanife.fr/github/fakemcserver/3.png)

## Usage
No arguments required to start. Optional arguments:
```
java -jar FakeMCServer.jar help - Display version
java -jar FakeMCServer.jar debug - Start with debug mode
```

## Configuration

Default configuration [available here](https://github.com/alkanife/FakeMCServer/blob/master/config.json).

| Name                | Type / color method                                                                  | Nullable? | Description                                                                                            | Default value                                          |
|---------------------|--------------------------------------------------------------------------------------|-----------|--------------------------------------------------------------------------------------------------------|--------------------------------------------------------|
| `host`              | String                                                                               | No        | Server host                                                                                            | 127.0.0.1                                              |
| `port`              | Int                                                                                  | No        | Server port                                                                                            | 25565                                                  |
| `base64_icon`       | String base64                                                                        | Yes       | Favicon, base64 must start with 'data:image/png;base64'. Show nothing if null.                         | null                                                   |
| `protocol`          | Int                                                                                  | /         | Server protocol/version (see [Protocol Version Numbers](https://wiki.vg/Protocol_version_numbers))     | 762 *(1.19.4)*                                         |
| `version_name`      | String ([legacy colors](https://minecraft.fandom.com/wiki/Formatting_codes))         | Yes       | Version name. Shown to the client if the client use a newer or older version than the protocol specify | Minecraft 1.19.4                                       |
| `motd`              | String ([Minimessage](https://docs.advntr.dev/minimessage/format.html))              | Yes       | Server MOTD                                                                                            | `<yellow>Fake Server\n<red>Maintenance!`               |
| `players`           | List of String ([legacy colors](https://minecraft.fandom.com/wiki/Formatting_codes)) | Yes       | Player sample (appears when hovering over the number of players)                                       | `["&eplayer1", "&6player2..."]`                        |
| `connected_players` | Int                                                                                  | /         | Connected "players"                                                                                    | 0                                                      |
| `max_players`       | Int                                                                                  | /         | Slots                                                                                                  | 0                                                      |
| `kick_message`      | String ([Minimessage](https://docs.advntr.dev/minimessage/format.html))              | Yes       | The message when a player try to connect                                                               | `<red>Sorry, the server is unavailable at the moment.` |

## Project dependencies
This project requires **Java 17+**.

- [google/**gson**](https://github.com/google/gson)
- [qos-ch/**logback**](https://github.com/qos-ch/logback)
- [kyori/**adventure-api**](https://docs.advntr.dev/)
- [kyori/**adventure-text-minimessage**](https://docs.advntr.dev/minimessage/index.html)
- [kyori/**adventure-text-serializer-gson**](https://docs.advntr.dev/serializer/gson.html)

## License
Under the [GNU Affero General Public License v3.0](https://github.com/alkanife-mc/FakeMCServer/blob/master/LICENSE) license.

