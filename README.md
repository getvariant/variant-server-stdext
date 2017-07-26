# Variant Experiment Server 0.7.1
## Server Extension API (ExtAPI) Sample Objects

[Variant Experiment Server](http://www.getvariant.com/docs/0-7/experiment-server/server-user-guide/) enables software developers conduct sophisticated, full-stack, feature-scoped online experiments on interactive, human facing computer applications. The need for such experiments frequently arises when it is desirable to measure business impact of a change to user experience. A typical example is an eCommerce Web application: a change to the check-out experience will likely have a direct impact on sales. (While a change to an FAQ page will not.) Variant server enables application developers to validate a proposed update to user experience by running it in parallel with the existing experience, as a randomized controlled experiment (RCE), where the update is the treatment and the existing experience servers as the control.

Variant server’s functionality can be extended through the use of the [server-side extension API, or ExtAPI](http://www.getvariant.com/docs/0-7/experiment-server/server-user-guide/#section-8), which provides a systematic mechanism for injecting custom semantics into the server’s regular execution path. 

Variant server's ExtAPI is provided in the variant-server-extapi-<release>.jar, found in this repository in [/lib](https://github.com/getvariant/variant-server-extapi/blob/0.7.1/lib) along with its dependent libraries. Refer to [Variant Server Reference Guide](http://www.getvariant.com/docs/0-7/experiment-server/reference/#section-3.1) for details on how to setup your ExtAPI development Environment.

#### Requires Java 7 or later.
Updated on 19 July 2017 for release 0.7.1.
