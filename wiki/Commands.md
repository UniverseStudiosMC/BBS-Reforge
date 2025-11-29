# Commands

All commands start with `/bbs`.

## Morphing

### `/bbs morph <targets> <formJson>`
Morph players into a form.
- Omit `<formJson>` to demorph

### `/bbs morph_entity`
Copy the looked-at mob into a form.

---

## Films

### `/bbs films <targets> play <filmId> [camera]`
Play a film for specified players.
- Add `camera` flag to also control their camera

### `/bbs films <targets> stop <filmId>`
Stop film playback.

---

## Model Blocks

### `/bbs model_block play_state <x> <y> <z> <state>`
Trigger an animation state on a model block.

### `/bbs model_block refresh <random_range>`
Resync all model blocks within range.

---

## Damage Control

### `/bbs dc start`
Enable damage control (world state tracking).

### `/bbs dc stop`
Pause damage control.

### `/bbs dc shutdown`
Disable and restore world state.

---

## Utility

### `/bbs on_head`
Move held item to helmet slot.

### `/bbs boom <x> <y> <z> <radius> <fire>`
Create an explosion.
- `fire`: `true` or `false`

### `/bbs structures save <name> <from> <to>`
Save a structure between two coordinates.

---

## Admin

### `/bbs config set <category.option> <value>`
Modify settings.
- Requires permission level 4

### `/bbs cheats <true|false>`
Toggle singleplayer cheats.

---

## Game Rules

### `bbsEditing`
Controls editor access on servers.
- Default: `true`
- Set via `/gamerule bbsEditing false`
