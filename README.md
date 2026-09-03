# libgcode

A scala library to parse, transform, and print g-code files

## Status

I just started on that.
For the moment, it just parses and prints basic g-code files.
There are limitations to the kind of gcode can be parsed/represented.
Currently, modal commands not supported.
See "ToDo" below for more details.

## Viewing the results

A simple G-Code viewer that can be used is [nc_viewer](https://github.com/dzufferey/nc_viewer).
It can watch files and automatically refresh the view when the file changes.
So the recommended workflow is to have a program which saves the result in a file and watch that file with the viewer.

```sh
cd nc_viewer
npm install
npm start -- my-program.nc
```

The [examples](examples/README.md) folder contains a few programs that
save the result in a file, ready to be watched this way.

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

