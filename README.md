TODO (features)
===============
* More sources (see below)
* Full text search
* Work on patrol/delete
* Archive articles after some amount of time
* License revocation and/or expiry
* Central server separate from client, for release
* Custom articles
    * Have an entire tag dedicated to them in addition to being found in the related tags
    * Written by us
        * Also allow others to write their own.
    * HTML
    * Stickied-Will be at the top of every tag list
    * Has the ability to link to other articles
* Wikipedia articles
    * At least one page/tag.
    * HTML
        * No Images
    * Similarly to custom articles, it will be stickied at the top of the tag page.
* User levels
    * Basic User
        * Search
        * Browse
    * Moderator extends BasicUser
        * Patrol
        * Can create basic users
        * Search can use SQL queries
            * Uses a sql user that only has SELECT
    * Admin extends Moderator
        * Modifies user priveleges
        * Modify home page text

TODO (dev)
==========
* Possibly find fixes for taginput problems
* Figure out what the proper way is to divide Guice modules
* More unit tests... there is practically no coverage

Sources
-------

### Tier 1
1. ~~New York Times~~
2. ~~Washington Post~~
3. ~~Christian Science Monitor~~
4. ~~Wall Street Journal~~
5. ~~Reuters~~
6. ~~BBC~~ 
7. ~~The Guardian~~
8. ~~The Economist~~
9. Al Jazeera
10. LA Times
11. Boston Globe
12. Seattle Times

### Tier 2
1. American Foreign Press
2. NPR
3. Jerusalem Post
4. Globe and Mail

### Skip
1. Associated Press
2. International Herald Tribune

