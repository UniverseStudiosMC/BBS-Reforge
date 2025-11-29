# Getting Started

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.1
2. Download Fabric API 0.105.0+
3. Download BBS from [Modrinth](https://modrinth.com/mod/mod-bbs-1.21.1)
4. Place `bbs.jar` in your `mods/` folder
5. Launch the game

## First Launch

On first launch, BBS creates:
- `config/bbs/assets/` - Place your custom assets here
- `config/bbs/settings/` - Configuration files

## Default Keybinds

| Key | Action |
|-----|--------|
| `0` | Open Dashboard |
| `HOME` | Item/Model Editor |
| `RIGHT_CTRL` | Play Film |
| `\` | Pause Film |
| `RIGHT_ALT` | Record Replay |
| `F4` | Record Video |
| `RIGHT_SHIFT` | Open Replays |
| `B` | Open Morphing Panel |
| `.` | Demorph |
| `Y` | Teleport |
| `Mouse Button 2` | Zoom |

## Basic Workflow

### 1. Create a Film

Films are the core container for your cinematic projects. A film contains:
- Camera clips (your camera movements)
- Replays (recorded player actions)

### 2. Record a Replay

1. Press `RIGHT_ALT` to start recording
2. Perform actions (walk, fight, interact)
3. Press `RIGHT_ALT` again to stop
4. Your replay is saved to the current film

### 3. Add Camera Clips

1. Open the Dashboard (`0`)
2. Navigate to the Film Editor
3. Add camera clips to the timeline
4. Configure clip properties (position, rotation, duration)

### 4. Play Your Film

1. Press `RIGHT_CTRL` to play
2. Press `\` to pause
3. Use the timeline to scrub through

## Server Setup

- Install BBS on both server and clients
- The game rule `bbsEditing` controls editor access (default: `true`)
- Permissions are checked via `PermissionUtils`

If the server doesn't have BBS, clients fall back to client-only playback mode.
