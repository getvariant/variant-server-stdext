# Variant Experiment Server 0.7.1
## Server Extension API (ExtAPI) Sample Objects
### Requires Java 7 or later.

Variant Experiment Server enables software developers conduct sophisticated, full-stack, feature-scoped online experiments on interactive, human facing computer applications. The need for such experiments frequently arises when it is desirable to measure business impact of a change to user experience. A typical example is an eCommerce Web application: a change to the check-out experience will likely have a direct impact on sales. (While a change to an FAQ page will not.) Variant server enables application developers to validate a proposed update to user experience by running it in parallel with the existing experience, as a randomized controlled experiment (RCE), where the update is the treatment and the existing experience servers as the control.

Variant server’s functionality can be extended through the use of the [server-side extension API, or ExtAPI](http://www.getvariant.com/docs/0-7/experiment-server/server-user-guide/#section-8), which provides a systematic mechanism for injecting custom semantics into the server’s regular execution path. 

Variant server's ExtAPI is provided in the variant-server-extapi-<release>.jar, found in this repository in [/lib] (https://github.com/getvariant/variant-server-extapi/blob/0.7.1/lib). It has one proprietary transitive dependency, the <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extapi/blob/master/lib/variant-core-0.7.1.jar" target="_blank">variant-core-&lt;release&gt;.jar&nbsp;<i class="fa fa-external-link"></i></a></span>. You may either directly import them into your project or, if you use a dependency management tool like Maven, install them into company's repository. You may also install them in your local Maven repository:

<pre class="variant-code">
% mvn install:install-file -Dfile=/path/to/variant-server-extapi-&lt;release&gt;.jar -DgroupId=com.variant \
                -DartifactId=variant-server-extapi -Dversion=&lt;release&gt; -Dpackaging=jar
</pre>

<pre class="variant-code">
% mvn install:install-file -Dfile=/path/to/variant-core-&lt;release&gt;.jar -DgroupId=com.variant \
                -DartifactId=variant-core -Dversion=&lt;release&gt; -Dpackaging=jar
</pre>
