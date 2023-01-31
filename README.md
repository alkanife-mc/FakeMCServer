<h1 align="center">
  FakeMCServer
  <br>
  <a href="https://github.com/alkanife/alkabot/blob/main/pom.xml">
    <img src="https://img.shields.io/badge/Open%20JDK-17-green" alt="JDK 17">
  </a>
  <a href="https://github.com/alkanife-mc/FakeMCServer/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/alkanife-mc/FakeMCServer" alt="LICENSE">
  </a>
  <a href="https://github.com/alkanife-mc/FakeMCServer/">
    <img src="https://img.shields.io/badge/version-1.1-blue" alt="version">
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

`host`: Fake server host (127.0.0.1 by default)

`port`: Fake server port (25565 by default)

`base64_icon`: Favicon, base64 must start with 'data:image/png;base64'. Show nothing if null

`protocol`: Fake server protocol/version (see https://wiki.vg/Protocol_version_numbers)

`version_name`: Version name. Shown to the client if the client use a newer or older version than the protocol specify

`players`: Player sample (appears when hovering over the number of players)

### Example:
````json
{
  "host": "127.0.0.1",
  "port": 25565,
  "base64_icon": "data:image/png;base64,...",
  "protocol": 761,
  "version_name": "Minecraft 1.19.3",
  "motd": [
    "&efakeserver.org", "&cMaintenance!"
  ],
  "players": ["&cMaintenance mode!"],
  "connected_players": 0,
  "max_players": 0,
  "kick_message": [
    "Sorry, the server is unavailable at the moment."
  ]
}
````

## Project dependencies
This project requires **Java 17+**.

- [google/**gson**](https://github.com/google/gson)
- [qos-ch/**logback**](https://github.com/qos-ch/logback)

## License
Under the [GNU Affero General Public License v3.0](https://github.com/alkanife-mc/FakeMCServer/blob/master/LICENSE) license.

