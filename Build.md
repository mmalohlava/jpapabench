# jPapaBench Project Structure #

The jPapaBench project consists of six modules:
  * _jpapabench-build_ - build project
  * _jpapabench-core_ - implementation of airplane control logic independent on a target platform
  * _jpapabench-core-flightplans_ - implementation of various flightplans
  * _jpapabench-pj_ - plain Java implementation encapsulating jpapabench-core
  * _jpapabench-rtsj_ - RTSJ-based implementation encapsulating jpapabench-core
  * _jpapabench-scj_ - SCJ-based implementation encapsulating jpapabench-core

The jpapabench-core and jpapabench-core-flightplans modules are independent on a target platform. They are implemented in plain Java. The modules jpapabench-{pj,rtsj,scj} encapsulate the core modules with help of a particular technology (plain Java, RTSJ, SCJ).

# jPapaBench Build #

For building jPapaBench implementation use module jpapabench-build. It includes a build file.

Running command `ant -p` shows possible build targets:
```

```