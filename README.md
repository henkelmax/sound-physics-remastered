# Sound Physics Remastered

> This is a fork of [Sound Physics Fabric](https://github.com/vlad2305m/Sound-Physics-Fabric) by [vlad2305m](https://github.com/vlad2305m) which is a fork of [Sound Physics](https://github.com/sonicether/Sound-Physics) by [Sonic Ether](https://github.com/sonicether).

A Minecraft mod that provides realistic sound attenuation, reverberation, and absorption through blocks.

Optimized for the use with [Simple Voice Chat](https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat) 2.X.X.


### Requirements
This mod does not require any dependencies, but it is recommended to use the following:

**Fabric**

- [ModMenu](https://modrinth.com/mod/modmenu)
- [ClothConfig](https://modrinth.com/mod/cloth-config)

**Forge**

- [ClothConfig](https://modrinth.com/mod/cloth-config)

---

### Changes to the Original Mod

**Improvements**

- Improved sound processing performance by a factor of 10
- Optimized for [Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat)
- Ported the mod to Forge
- Improved configuration UI
- Made Cloth Config optional
- Tweaked default config values
- Added reflectivity to the config GUI
- Added occlusion variation
- Added block occlusion factor config
- Added debug sound bounce rendering
- Added debug sound occlusion rendering

**Bugfixes**

- Fixed sounds not bouncing more than once
- Fixed sound bouncing using an excessive amount of performance, despite not working at all
- Fixed direction evaluation not taking sound source and primary bounces into account
- Fixed sound allowance not working for `/playsound` command
- Fixed unmapped field names in the reflectivity config
- Fixed sound processing using player position instead of camera position