This mod was made for ModJam 5 in three days, so forgive any bugs! 
To see the code as it was at the end of the ModJam check the [ModJam5 branch](https://github.com/suppergerrie2/ModJam5/tree/ModJam5)

You can also join a discord server [here](https://discord.gg/VHD4Ptm) this server is for suppergerrie2.com but also contains channels for this mod.

Download the mod at the [curseforge page](https://minecraft.curseforge.com/projects/suppergerrie2s-drone-mod).

# Basic information

Adds five types of drones:
1. Hauler drone: searches items in the world and brings them to its home.
2. Fighter drone: fights mobs around it.
3. Tree farm drone: plants trees and chops them down.
4. Crop farm drone: prepares farmland, plants seeds and breaks them when fully grown.
5. Archer drone: shoots hostile mobs that come to close to it.
It sets it home where you place it. 

This mod also adds a drone stick this can do a couple different things:
1. Select drones by right clicking on them (Deselect by shift-rightclicking in air)
2. Set home of selected drones by right clicking on block.
3. Kill drone by shift-right clicking on drone.

TODO:
- [x] Filter
- [x] Carry upgrade
- [ ] Mining drone
- [ ] Transport drone
- [x] Crop farm drone
- [ ] Upgrades (Check issue #16)

# Want to help?

### Found a bug or have an idea?
Report the bug (or your idea!) by clicking [here](https://github.com/suppergerrie2/ModJam5/issues/new).
- Make sure the bug or idea hasn't been posted before by checking the other [issues](https://github.com/suppergerrie2/ModJam5/issues).
- Please check if the bug occurs without any other mods installed!

### Want to help develop the mod?
1. Make sure you have git installed. [Github help page](https://help.github.com/articles/set-up-git/)
2. Fork this repository by clicking fork in the top right. [Github help page](https://guides.github.com/activities/forking/)

If you just want to translate or don't need to launch the game you can start editing now! Else follow the instructions to setup a forge environment.

3. Download an IDE like [eclipse](https://www.eclipse.org/home/index.php) and install it.
4. Setup a forge environment by running `gradlew setupDecompWorkspace`
   If you are using eclipse you also need to run `gradlew eclipse`
5. Now launch your IDE and import the folder you cloned this repository in as a project.

After making the changes you can send a pull request! [Github help page](https://guides.github.com/activities/forking/)

Now you can also launch the game and edit the code! For more information you can go to [my modding tutorial](https://www.suppergerrie2.com/minecraft-1-12-modding-with-forge-1-getting-started/)
