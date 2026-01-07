# Run It Back

Run It Back is a Minecraft mod that adds a “retry mining” mechanic. When enabled, certain blocks require multiple attempts to successfully break. This can be configured per block or block tag.

How it works:
- When a player mines a block in the configured list, the block may not break immediately.
- The mod tracks how many times a block has been mined at a specific location.
- Once the configured number of attempts is reached, the block will finally break.
- Creative players and players with the bypass permission can be excluded from this mechanic.

Config options:
```
enabled: Boolean - Whether the mod is active.
count: Integer - How many times a block needs to be mined before it breaks.
creative: Boolean - Whether creative mode players bypass the mechanic.
bypassPermission: String - Permission string allowing a player to bypass the mechanic.
permission: String - Permission string required to run the mod commands.
list: List - Blocks or tags affected. Use `#id:tag` for tags and `#id:block` for blocks.
```

Commands:
```
/run-it-back toggle
Toggles the mod on or off.

/run-it-back reload
Reloads the mod configuration from the config file.
```

Permissions:
```
run-it-back.bypass
Players with this permission bypass the retry mining mechanic.

run-it-back.command
Players with this permission can use /run-it-back commands.
```

Example:
- A config with `count = 4` means the first three mining attempts fail, and the fourth succeeds.
- A block list including `minecraft:diamond_ore` means diamond ore will require multiple mining attempts.
