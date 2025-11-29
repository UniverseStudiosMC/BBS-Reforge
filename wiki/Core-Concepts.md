# Core Concepts

## Forms

Forms are the visual representation system in BBS. They define how entities, morphs, and model blocks appear.

### Form Types

| Type | Description |
|------|-------------|
| `model` | Custom 3D models (OBJ, BOBJ, VOX, Cubic) |
| `mob` | Vanilla mob appearances |
| `block` | Block as a form |
| `item` | Item as a form |
| `billboard` | 2D sprite facing the camera |
| `label` | Text display |
| `particle` | Particle effects |
| `extruded` | Extruded 2D textures |
| `anchor` | Reference point |
| `trail` | Motion trails |
| `framebuffer` | Screen capture display |

### Form Properties

- **Transforms**: Position, rotation, scale (base/overlay/first-person/third-person)
- **Hitbox**: Custom collision box
- **Body Parts**: Attachable sub-forms
- **Animation States**: Triggered animations

---

## Films

A Film is a complete cinematic project containing:

- **Camera Clips**: Camera movements and effects
- **Replays**: Recorded player performances
- **Snapshots**: Inventory, HP, XP states

Films are stored as compressed `.dat` files in `<world>/bbs/films/`.

---

## Camera System

### Camera Clips

| Clip Type | Description |
|-----------|-------------|
| `idle` | Static camera position |
| `dolly` | Linear movement between points |
| `path` | Curved path through multiple points |
| `keyframe` | Keyframe-based animation |

### Camera Modifiers

Modifiers alter camera behavior:

| Modifier | Effect |
|----------|--------|
| `translate` | Offset position |
| `angle` | Adjust rotation |
| `drag` | Smooth following |
| `shake` | Camera shake effect |
| `math` | Mathematical expressions |
| `look` | Look at target |
| `orbit` | Orbit around point |
| `remapper` | Time remapping |
| `tracker` | Track entity |
| `dolly_zoom` | Vertigo effect |

### Media Clips

| Type | Purpose |
|------|---------|
| `audio` | Play sound |
| `subtitle` | Display text |
| `curve` | Animation curve |

---

## Replays & Actions

### Replays

A Replay stores:
- **Keyframes**: Position, rotation, slots, armor, flags per tick
- **Properties**: Shadow, looping, relative offsets
- **Action Clips**: Gameplay events

### Action Types

| Action | Description |
|--------|-------------|
| `chat` | Send chat message |
| `command` | Execute command |
| `place_block` | Place a block |
| `break_block` | Break a block |
| `interact_block` | Interact with block |
| `use_item` | Use held item |
| `drop_item` | Drop item |
| `attack` | Attack entity |
| `damage` | Receive damage |
| `swipe` | Arm swing |

### Damage Control

Damage Control restores world state after playback:
- Blocks broken during replay are restored
- Entities killed are respawned
- Enable via settings or `/bbs dc start`

---

## Blocks

### Model Block

Renders any Form as a block in the world.
- Supports per-view transforms
- Multiple visual variants
- Animation state triggers

### Chroma Blocks

8 solid color blocks for green-screen compositing.

---

## Gun System

Configurable projectile weapons with:

| Property | Description |
|----------|-------------|
| `scatter` | Spread pattern |
| `speed` | Projectile velocity |
| `gravity` | Gravity effect |
| `bounce` | Bounce count |
| `damage` | Hit damage |
| `knockback` | Push force |
| `impact` | Impact command/form |
| `zoom` | Zoom form/command |
| `projectile` | Custom projectile form |

---

## Morphing

Players can morph into any Form:
- Applies visual appearance
- Modifies attributes (speed, health, etc.)
- Plays form animation states
- Use `/bbs morph` or press `B` for the panel
