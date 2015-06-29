# Distiller-CORE library #
 
Distiller is a library to extract and infer knowledge from text. Distiller takes his roots from DIKpE [1] and further evolutions [4], but is improved with multilanguage support, entity linking with [2] and concept inference. By now, Distiller supports only keyphrase Italian and English; we plan to include support for keyphrase extraction in other languages, such as Portuguese, Romanian and Arabic.

The default Distiller pipeline works on keyphrase extraction; anyways, since the library is built with extensibility in mind, it's possible to extend it and write pipelines for any high-level NLP task. As an example, we include a simple Sentiment Analysis module, based on M.L. Jokers' Syuzhet library [3].

## Architecture ##

The architecture of the library and its usage will be explained in an upcoming paper.

## Build ##

Distiller, by now, is distibuted as a Netbeans project. You can open it in Netbeans or compile it yourself (we won't provide a guide, since it's [just a simple Maven project](http://maven.apache.org/archives/maven-1.x/start/quick-start.html)). A download link for the standalone .jar is coming soon. Hopefully, we'll also publish the library to Maven Central, to make it easier to use Distiller in your projects.

## Acknowledgements ##

The "dirty work" in the library is handled mainly by three libraries:
- [Shuyo Nakatani's Language Detection Library for Java](https://github.com/shuyo/language-detection/)
- [Apache OpenNLP](https://opennlp.apache.org/);
- [The Tartarus Snowball Stemmer](http://snowball.tartarus.org/).

## References ##

[1] Pudota, Nirmala, et al. "Automatic keyphrase extraction and ontology mining for content‐based tag recommendation." International Journal of Intelligent Systems 25.12 (2010): 1158-1186.

[2] Paolo Ferragina, Ugo Scaiella. "Fast and Accurate Annotation of Short Texts with Wikipedia Pages". IEEE Software 29(1): 70-75 (2012).

[3] https://github.com/mjockers/syuzhet

[4] De Nart, Dario, and Carlo Tasso. "A domain independent double layered approach to keyphrase generation." WEBIST 2014-Proceedings of the 10th International Conference on Web Information Systems and Technologies. 2014.

