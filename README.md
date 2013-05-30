COS314-Project
==============

2-Player Board Game playing AI using Neural Networks, Learning from Zero Knowledge

Building
--------

1. Change Directory into the project's root folder. (The folder this README is in)
2. Execute "ant jar"
3. If the build was successful, you should find "COS314-Project.jar" in the "dist" folder.

Running
-------

Program usage:

    java -jar dist/COS314-Project.jar COMMAND [ -o OUTPUT_FILE ] [ -f INPUT_FILE ] [ -i NUM_ITERATIONS ]

The available COMMANDs are as follows:

    play - Play against a trained neural network.

    train - Train a neural network.

The flags are explained below:

    -o
    Specifies the output file. This should only be used with the train command.

    -f
    Specifies the input file. This must be present when using the play command. However it can be used with the train command to "continue" training an existing population.
    If this switch is present when using the train command, an output file is not necessary (it will use the input file as the output file).

    -i
    Specifies a number of iterations to train. This should only be used with the train command. If not present, the default of 500 will be used.
