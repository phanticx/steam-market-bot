# steam-market-bot
Discord bot mainly using Java and Selenium to scrape price, wear, and other data of CS:GO skins on the Steam Marketplace with use of the [CSGOFloat API](https://github.com/csgofloat/inspect).
If you plan on heavily using this bot, please host the CSGOFloat Inspect Link Repo linked above.

As of right now, I have little to no idea what I am doing but the code should work with some tinkering.

## Usage
* Create a new discord bot in the [Discord Developer Portal](https://discord.com/developers/applications).
* Change YOUR_TOKEN in [Main.java](https://github.com/phanticx/steam-market-bot/blob/main/bot/src/main/java/Main.java) to your bot token.
* __Avaliable Commands__
  * **!help**
    * Syntax: !help or !help <command name>
  * **!info**
    * Syntax: !info
  * **!checkskin**
    * Syntax: !checkskin <result count (0-100)> <url> <options (each separated with a space)>
    * Options:
      * sort_float_asc: Sort by lowest float
      * sort_float_dsc: Sort by highest float
      * sort_price_asc: Sort by lowest price
      * sort_price_dsc: Sort by highest price
      * filter_float: Filter by float, greater or less than a specified value with the operators ">" or "<" (ex: filter_float>0.5)
      * filter_price: Filter by price, greater or less than a specified value with the operators ">" or "<" (ex: filter_price>0.5)

## Changelog

v1.1.0
* Retrieves data more efficiently by eliminating unnecessary requests.
* !checkskin command updates
  * New options to !checkskin command - filter_float & filter_price.
  * Revises sort options to allow for ascending and descending orders.
* Adds elapsed time to run command.
* Small spelling mistakes corrected.

v1.0.0
* Initial Unfinished Release
