# steam-market-bot
Discord bot mainly using Java and Selenium to scrape price, wear, and other data of CS:GO skins on the Steam Marketplace with use of the [CSGOFloat API](https://github.com/csgofloat/inspect).
If you plan on heavily using this bot, please host the CSGOFloat Inspect Link Repo linked above.

As of right now, I have little to no idea what I am doing but the code should work with some tinkering.\

## Usage
* Change YOUR_TOKEN in [Main.java](https://github.com/phanticx/steam-market-bot/blob/main/bot/src/main/java/Main.java) to your bot token in the [Discord Developer Portal](https://discord.com/developers/applications).
* __Avaliable Commands__
  * **!help**
    * Syntax: !help or !help <command name>
  * **!info**
    * Syntax: !info
  * **!checkskin**
    * Syntax: !checkskin <result count (0-100)> <url> <options>
    * Options:
      * sort_float: Sort by lowest float
      * sort_price: Sort by lowest price



## Changelog
v 1.0.0
* Initial Release

