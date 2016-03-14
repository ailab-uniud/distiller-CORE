# Distiller-CORE library #
 
Distiller is a framework to extract and infer knowledge from texts. Distiller takes its roots from DIKpE [1] and further evolutions [4], but it is improved with multilanguage support [5], entity linking with [2] and concept inference. By now, Distiller supports only keyphrase extraction in Italian and English; we plan to include support for keyphrase extraction in other languages.

The default Distiller pipeline works on keyphrase extraction; anyways, since the framework is built with extensibility in mind, it's possible to extend it and write pipelines for any high-level NLP task. As an example, we include a simple Sentiment Analysis module, based on M.L. Jokers' Syuzhet library [3].

## Architecture ##

The architecture of the framework and its usage is described in "Introducing Distiller: a unifying framework for Knowledge Extraction" , 1st AI*IA Workshop on Intelligent Techniques At Libraries and Archives, 2015 [(download link)](http://ceur-ws.org/Vol-1509/ITALIA2015_paper_4.pdf).

## How to build and use the Distiller ##

Inside the Wiki we have some guides on [downloading, building and using Distiller](https://github.com/ailab-uniud/distiller-CORE/wiki/How-to-build-Distiller). 

Distiller, by now, is distibuted in source code form only. You can open it in your favourite IDE or compile it yourself, since it's [just a simple Maven project](http://maven.apache.org/archives/maven-1.x/start/quick-start.html)). When we'll reach a stable enough codebase, we'll also publish Distiller to Maven Central or another Maven repository, to make it easier to use it in your projects.

Please note that to use some features of Distiller you should install also [R](https://www.r-project.org/).

## Acknowledgements ##

The "dirty work" in the library is handled mainly by three libraries:
- [Shuyo Nakatani's Language Detection Library for Java](https://github.com/shuyo/language-detection/);
- [Apache OpenNLP](https://opennlp.apache.org/);
- [Stanford CoreNLP](http://nlp.stanford.edu);
- [R](https://www.r-project.org/) and the [R-caller library](https://github.com/jbytecode/rcaller);
- [The Tartarus Snowball Stemmer](http://snowball.tartarus.org/).

The Italian language implementation of the Distiller is made possible by:
- [Andrea Ciapetti's OpenNLP models](https://github.com/aciapetti/opennlp-italian-models) for sentence splitting, tokenization and PoS tagging;
- [Morph-it!](http://sslmitdev-online.sslmit.unibo.it/linguistics/morph-it.php) for lemmatization.

## Citing ##

If you use Distiller, please cite this paper:

```tex
@inproceedings{distillerintroducing,
  title={Introducing Distiller: a unifying framework for Knowledge Extraction},
  author={Basaldella, Marco and De Nart, Dario and Tasso, Carlo},
  year={2015},
  booktitle={Proceedings of 1st AI*IA Workshop on Intelligent Techniques At Libraries and Archives co-located with XIV Conference of the Italian Association for Artificial Intelligence (AI*IA 2015)},
  organization={Associazione Italiana per l'Intelligenza Artificiale},  
  year={2015}
}
```

## License ##

This program is free software; you can redistribuite it and/or modify it under the terms of the GNU/General Pubblic License as published the Free software Foundation; either version 2 of the License, or (at your opinion) any later version.

## References ##

[1] Pudota, Nirmala, et al. "Automatic keyphrase extraction and ontology mining for content‐based tag recommendation." International Journal of Intelligent Systems 25.12 (2010): 1158-1186.

[2] Paolo Ferragina, Ugo Scaiella. "Fast and Accurate Annotation of Short Texts with Wikipedia Pages". IEEE Software 29(1): 70-75 (2012).

[3] https://github.com/mjockers/syuzhet

[4] De Nart, Dario, and Carlo Tasso. "A domain independent double layered approach to keyphrase generation." WEBIST 2014-Proceedings of the 10th International Conference on Web Information Systems and Technologies. 2014.

[5] 	Dante Degl'Innocenti, Dario De Nart, Carlo Tasso. "A New Multi-lingual Knowledge-base Approach to Keyphrase Extraction for the Italian Language". KDIR 2014: 78-85
