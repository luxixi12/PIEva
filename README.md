PIEva
=====

 PIEva employs a support-free algorithm TRkMiner to discover the top-rank-𝑘 patterns. TRkMiner is equipped with a novel interestingness metric that considers both size and support of a pattern to ensure the quality of discovered patterns. In the samples labeling phase, RPInteract selects representative patterns from the discovered pattern set based on the representativeness of patterns and obtains users’ preferences on the selected patterns via limited interaction. On the labeled samples, PIEva trains a prediction model to forecast patterns’ subjective interestingness.


CONTENTS:
=====

    TRkMiner ...................  frequent pattern mining
    RPInteract .................  learn and forecast patterns’ subjective interestingness
    Datasets/ ..................  Example graphs

REQUIREMENTS:
=====

Java JRE v17.0.8 and Python 3.9
