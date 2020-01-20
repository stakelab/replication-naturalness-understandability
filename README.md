# Replication Package
This package contains all the data used in this study. The main file is `dataset.csv`, which contains the complete dataset with understandability and naturalness metrics. It contains the following columns:

- `developer`: ID of the developer who evaluated the snippet;
- `topic`: the topic of the snippet;
- `title`: the signature of the snippet;
- `system_name`: the name of the system from which the snippet was selected;
- `topic_experience`: discretized developers' experience on the topic (values: `top`, `medium`, `bottom`);
- `experience_objective`: objective experience (see below);
- `experience_subjective`: subjective experience (see below);
- `binary_naturalness`: binary developer-based naturalness;
- `naturalness`: continuous developer-based naturalness;
- `global_naturalness`: binary naturalness computed with a global model;
- `correctness`: percentage of correct answers to the questions about the snippet;
- `understood`: perceived understanding of the snippet (values: `0` - not understood, or `1` - understood);
- `seconds_needed`: seconds needed to evaluate the snippet;
- `deceptiveness`: continuous deceptiveness of the snippet for the developer.

## Developers' Experience
The folder `developers-experience` contains two files:

- `subjective-experience.csv`: self-assessed developers' experience on different topics (5-point Likert scale, between 1 and 5, or 0 if they have no experience at all);
- `objective-experience.csv`: developers' experience on different topics measured in terms of percentage of correct answers to topic-related questions (between 0 and 1).

## Snippets
The folder `snippets` contains all the Java snippets used in the context of this study. This folder also contains the file `questions.txt`, with the complete list of questions used to evaluate developer-based understandability.
