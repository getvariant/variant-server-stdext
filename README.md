# Variant Experiment Server Extension API (ExtAPI) Sample Objects
### Release 0.8.1
#### Requires: Java 8 or later

[__Documentation__](http://www.getvariant.com/docs/0-8/experiment-server/user-guide/#section-9) | [__Javadoc__](http://www.getvariant.com/javadoc/0.8/)

[Variant Experiment Server](http://www.getvariant.com/docs/0-8/experiment-server/user-guide/) enables software developers conduct sophisticated, full-stack, feature-scoped online experiments on interactive, human facing computer applications. The need for such experiments frequently arises when it is desirable to measure business impact of a change to user experience. A typical example is an eCommerce Web application: a change to the check-out experience will likely have a direct impact on sales. Variant server enables application developers to validate a proposed update to user experience by running it in parallel with the existing experience, as a [randomized controlled experiment](https://en.wikipedia.org/wiki/Randomized_controlled_trial), where the update is the treatment and the existing experience servers as the control.

Variant server’s functionality can be extended through the use of the [server-side extension API, or ExtAPI](http://www.getvariant.com/docs/0-8/experiment-server/user-guide/#section-9), which provides a systematic mechanism for injecting custom semantics into the server’s regular execution path. 

Variant server's ExtAPI is provided in the variant-server-extapi-\<release\>.jar, found in this repository in [/lib](https://github.com/getvariant/variant-server-extapi/tree/master/lib) along with its dependent libraries. Refer to [Variant Server Reference Guide](http://www.getvariant.com/docs/0-8/experiment-server/reference/#section-4.1) for details on how to setup your ExtAPI development Environment.
