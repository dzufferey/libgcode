# libgcode

A scala library to parse, transform, and print g-code files

## Status

I just started on that.
For the moment, it just parses and prints basic g-code files.
There are limitations to the kind of gcode can be parsed/represented.
Currently, modal commands not supported.
See "ToDo" below for more details.

## Using libgcode with Jupyter

This is the way I'm currently using it.

1. [install Jupyter](https://jupyter.org/install)
2. [install Almond](https://almond.sh/docs/quick-start-install)
3. run `sbt publishLocal` in this folder
4. open the file [notebook/samples.ipynb](notebook/samples.ipynb) in Jupyter to see examples

## ToDo

#### Better support of modal commands

TL;DR it works better if the commands start with `G`, `M`, or `O`.
Use `Empty` when the command is not specified.

When I started this, I was only looking at g-code from 3D printer slicer.
They all followed the restriction above so I incorrectly assumed it was like that.
Later, when I started to play with CNC routers I learned about modal commands (e.g. a line to set the feed without any motion command, not repeating G1).

#### Extractors

Some more extractors for commands.
For instance:
* `case G(1, params) => ...`.
* `case G(1.5, X(var1) :: remainingParams ) => ...`.
* ...

#### Examples of Transformers/Transducers

A few sample classes of g-code transformers:
* remove comments
* sanitization / well-formedness check
* change unit (inch to mm and return)
* scale, translate, rotate
* circular motion to segments (given a maximal acceptable error)

#### Abstract Machine

have an abstract machine to simulate the code, i.e., get the state of the machine at any point in time.

Extend the abstract machine to
* estimate machining time
* keep track of
  - spindle: off | clockwise | counter-clockwise (RPM?)
  - coolant: off | mist | flood
  - ? cutter radius compensation
  - ? tool length offset
  - ? extruder(s) feedrate/position
  - ? value of other parameters

