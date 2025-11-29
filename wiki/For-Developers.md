# For Developers

## Source Structure

```
src/
├── main/java/mchorse/bbs_mod/       # Server + shared code
│   ├── actions/                     # Record/playback system
│   ├── camera/                      # Camera clips & controllers
│   ├── film/                        # Film management
│   ├── forms/                       # Form definitions
│   ├── network/                     # Networking payloads
│   ├── blocks/                      # Block definitions
│   ├── items/                       # Item definitions
│   ├── resources/                   # Asset management
│   └── utils/                       # Math, interpolation, keyframes
│
├── client/java/mchorse/bbs_mod/     # Client-only code
│   ├── ui/                          # Dashboard, editors, HUD
│   ├── rendering/                   # Renderers
│   └── network/                     # Client packet handlers
│
└── main/resources/
    ├── assets/bbs/                  # Bundled assets
    └── bbs.accesswidener            # Access widener
```

## Entry Points

| Side | Class |
|------|-------|
| Server | `src/main/java/mchorse/bbs_mod/BBSMod.java` |
| Client | `src/client/java/mchorse/bbs_mod/BBSModClient.java` |

---

## Adding a Camera Clip

1. Create a class extending `CameraClip`
2. Implement interpolation in `apply()` and `setup()`
3. Register in `BBSMod.getFactoryCameraClips()`:

```java
factory.register(new ClipFactoryData("my_clip", icon, color), MyClip::new);
```

---

## Adding an Action Clip

1. Create a class extending `ActionClip`
2. Implement `applyAction()` for server-side logic
3. Optionally implement `applyClientAction()` for client-only behavior
4. Register in `BBSMod.getFactoryActionClips()`
5. Hook recording events via `ActionHandler` if needed

---

## Adding a Form Type

1. Create a class extending `Form`
2. Define rendering and animation handling
3. Register in `FormArchitect` inside `BBSMod#onInitialize`:

```java
FormArchitect.register("my_form", MyForm::new);
```

---

## Networking

- Payloads: `network/payloads/`
- Server registration: `ServerNetwork`
- Client registration: `ClientNetwork`

Handshake determines if server has BBS. If not, `ClientNetwork.isIsBBSModOnServer()` returns `false`.

---

## Mixins

| File | Target |
|------|--------|
| `bbs.mixins.json` | Server/shared mixins |
| `bbs.client.mixins.json` | Client mixins (rendering, input, camera) |

Mixins patch:
- Camera override
- Morph rendering
- Input handling
- World updates
- Permission checks

---

## Building

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

Output: `build/libs/`

Dev run: `./gradlew runClient`

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Mod not detected on server | Check `ClientNetwork.isIsBBSModOnServer()` |
| Assets not updating | Verify watchdog is running (`BBSResources.setupWatchdog()`) |
| World edits persist | Enable damage control or use `/bbs dc shutdown` |
| Visual glitches | Clear/reload forms, check mixin conflicts |
