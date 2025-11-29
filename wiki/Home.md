# BBS Reforge

**BBS Reforge** is a Fabric 1.21.1 rebuild of the Blockbuster cinematic toolkit for Minecraft.

## What is BBS?

BBS (Blockbuster Studio) is a powerful machinima and cinematic creation mod that allows you to:

- **Create cinematic camera paths** with keyframes and modifiers
- **Record and replay player actions** as "actors"
- **Morph into any model** with custom forms
- **Build film sets** with model blocks and chroma blocks
- **Configure weapons** with the gun/projectile system
- **Capture video** directly in-game

## Quick Links

| Page | Description |
|------|-------------|
| [Getting Started](Getting-Started) | Installation and first steps |
| [Core Concepts](Core-Concepts) | Forms, Films, Camera, Replays |
| [Commands](Commands) | All available commands |
| [Configuration](Configuration) | Settings and file structure |
| [For Developers](For-Developers) | Extending BBS with custom content |

## Download

- [Modrinth](https://modrinth.com/mod/mod-bbs-1.21.1)

## Requirements

- **Minecraft** 1.21.1
- **Java** 21
- **Fabric Loader** 0.16.0+
- **Fabric API** 0.105.0+1.21.1

### Optional Compatibility

- Sodium
- Iris
- Indium
- Distant Horizons

## Project Structure

```
config/bbs/
├── assets/      # Custom models, textures, sounds
├── settings/    # Configuration files
├── export/      # Exported content
└── url_cache/   # Cached URL assets

<world>/bbs/
└── films/       # Per-world film data
```
