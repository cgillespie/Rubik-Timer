Rubik-Timer
===========

A basic competition style puzzle timer.



Definitions
-----------
**Puzzle** - A Puzzle represents a physical puzzle that could be solved including a scramble algorithm.
**Profile** - A Profile represents a style of solving a Puzzle. Whether it be for speed, with one hand, blindfolded, or with a certain solve method.


Custom Scramblers
-----------------
A custom scrambling algorithm can be made in Java by implementing the Scrambler interface. To make use of these scramblers the compiled `.class` file must be placed in its fully qualified directory structure within the `Scramblers` directory. The fully qualified path is made up of the package name of the class.

Eg.
 - `package com.example.myscramblers;` becomes `com\example\myscramblers\`
 - `package org.foo.scramble;` becomes `org\foo\scramble\`
