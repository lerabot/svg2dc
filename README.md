# svg2dc
svg to dreamcast engine exporter.

![alt text](https://github.com/lerabot/svg2dc/blob/master/svg2dc/img/screen1.png?raw=true "Screenshot")


This is a small utility that convert our maps (created in inkscape) in a C fonction that loads all the asset into our custom Dreamcast engine.
The program will check for any incompatible image size and will calculate the meory usage for you. It also keep tracks of filename and will only load any duplicated asset once.

### How to use
1. Select the *Output Folder* first. This will be where your asset will be copied along with the *mapdata.c* file.
2. Select your SVG. Once it is selected it will do all the job for you- copying your asset into the *Output Folder*, converting the object position and size into something usable for my unamed DC engine.

### XML special tag

![alt text](https://github.com/lerabot/svg2dc/blob/master/svg2dc/img/screen2.png?raw=true "Screenshot")

Here's some special XML tags that the software will catch
* u : expects a number between 1.0 and 0.0 - used for UV mapping.
* v : expects a number between 1.0 and 0.0 - used for UV mapping.
* setAnim : if the texture is a spritesheet, will select the appropriate sprite. expects a integer. you also need to set **u** and **v** for this to work

The program also detext if a images has been mirrored and will flip the UV map accordingly.

### Compatibility
If you're interested in using this for your project and want a less specific output than my custom engine, I'd be happy to try to make this more usable. Please fill an issue in or contact me at r.rabot_at_gmail.com


