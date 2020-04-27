# Docker

## Overview

Docker is an open platform for developing, shipping, and running applications.

Docker provides the ability to package and run an application in a loosely isolated environment called a container. The isolation and security allows you to run many containers simultaneously on a given host. Containers are lightweight because they don't need the extra load of a hypervisor, but run directly within the host machine's kernel. This means you can run more containers on a given hardware combination than if you were using virtual machines. You can even run Docker containers within host machines that are actually virtual machines.

Docker is written in `Go` and takes advantages of serval features of the Linux kernel to deliver its functionality.

## Concepts

- Flexible: Even the most complex applications can be containerized.
- Lightweight: Containers leverage and share the host kernel, making them much more efficient in terms of system resources than virtual machines.
- Portable: You can build locally, deploy to the cloud, and run anywhere.
- Loosely coupled: Containers are highly self sufficient and encapsulated, allowing you to replace or upgrade one without disrupting others.
- Scalable: You can increase and automatically distribute container replicas across a datacenter.
- Secure: Containers apply aggressive constraints and isolations to processes without any configuration required on the part of the user.

## Architecture

Docker uses a client-server architecture. The Docker client talks to the Docker daemon, which does the heavy lifting of building, running, and distributing your Docker containers. The Docker client and daemon can run on the same system, or you  can connect a Docker client to a remove Docker daemon. The Docker client and daemon communicate using a REST API over UNIX sockets or a network interface.

Client: `docker build`, `docker pull` `docker run`
DOCKER_HOST: `Docker daemon` which control containers, images, and talk to the registry

## Daemon

The Docker daemon (`dockerd`) listens for Docker API requests and manages Docker objects such as images, containers, networks, and volumes. A daemon can also communicate with other daemons to manage Docker services.

## Client

The Docker client (`docker`) is the primary way that many Docker users interact with Docker. When you use commands such as `docker run`, the client sends these commands to `dockerd`, which carries them out. The `docker` command uses the Docker API. The Docker client can communicate with more than one daemon.

## Registry

A Docker registry stored Docker images. Docker Hub is a public registry that anyone c an use, and Docker is configured to look for images on Docker Hub by default. You can even run your own private registry. If you use Docker Datacenter (DDC), it includes Docker Trusted Registry (DTR).

When you use the `docker pull` or `docker run` commands, the required images are pulled from your configured registry. When you use the `docker push` command, your image is pushed to your configured registry.

## Objects

Objects are images, containers, networks, volumes, plugins, etc.

## Images

An image is a read-only template with instructions for creating a Docker container. To build your own image, you create a Dockerfile with a simple syntax for defining the steps needed to create the image and run it, Each instruction in a Dockerfile creates a layer in the image, When you change the Dockerfile and rebuild the image, only those layers which have changed are rebuilt.

## Containers

A container is a runnable instance of an image. You can create, start, stop, move, or delete a container using the Docker API or CLI. You can connect a container to one or more networks, attach storage to it, or even create a new image based on its current state.

By default, a container is relatively well isolated from other containers and its host machine. You can control how isolated container's network, storage, or other underlying subsystems are from other containers or from the host machine.

A container is defined by its image as well as any configuration options you provide to it when you create or start it. When a container is removed, any changed to its state that are not stored in persitent storage disappear.

## `docker run`

`docker run -i -t ubuntu /bin/bash`

When you run this command, the following happens (assuming you are using the default registry congiguration):
1. If you do not have the ubuntu image locally, Docker fulls it from your configured registry, as though you had run docker pull ubuntu manually.
1. Docker creates a new container, as though you had run a `docker container create` command manually.
1. Docker allocates a read-write filesystem to the container, as its final layer. This allows a running container to create or modify files and directories in its local filesystem
1. Docker creates a network interface to connect the container to the defualt etwork, since you did not specify any networking options. This includes assigning an IP address to the container. By default, containers can connect to external networks using the host machine's network connection.
1. Docker starts the container and executes `/bin/bash`. Because the container is running interactively and attached to your terminal (due to the -i and -t flags), you cna provide input using your keyboard while the output is logged to your terminal.
1. When you type `exit` to terminate the `/bin/bash` command, the container stops but is not removed. You can start it again or remove it.

`docker run --publish 8000:8080 --detach --name bb bulletinboard:1.0`

- `--publish` asks Docker to forward traffic incoming on the host's port 8000, to the container's port 8080. Containers have their own private set of ports, so if you want to reach one from the network, you have to forward traffic to it in this way. Otherwise, firewall rules will prevent all network traffic from reaching your container, as a default security posture.
- `--detach` asks Docker to run this container in the background
- `--name` specifies a name with which you can refer to your container in subsequent commands, in this case `bb`

`docker rm --force bb`

- `--force` option removes the running container. This can be left out if you stop the container running with `docker stop bb`, then `--force` is no longer needed

## Services

Services allow you to scale containers across multiple Docker daemons, which all work together as a `swarm` with multiple `managers` and `workers`. Each member of a swarm is a Docker daemon, and all the daemons communicate using the Docker API. A service allows you to define the desired state, such as the number of replicas of the service that must be available at any given time. By default, the service is load-balanced across all worker nodes. To the consumer, the Docker service appears to be a single applicaiton. Docker Engine supports swarm mode in Docker 1.12 and higher.

## Glossary

**Container**
- A standardized unit of software that packages up code and all its dependencies so the application runs quickly and reliably from one computing environment to another.
- An abstraction at the app layer that packages code and dependencies together. Multiple containers can run on a single machine and share the OS kernel with other containers, each running as isolated processes in user space.
- Standard: Docker created the industry standard for containers, so they could be portable anywhere
- Lightweight: Containers share the machine's OS system kernel and therefore do not require an OS per application, driving higher server efficiencies and reducing server and licensing costs
- Secure: Applications are safer in containers and Docker provides the strongest default isolation capabilities

**Container image**
- A lightweight, standalone, executable package of software that includes everything needed to run an application: code, runtime, system tools, system libraries and settings.
- Container images become containers at runtime - in the case of Dockers containers, images become containers when they run on Docker Engine.

**Kernel**
- A computer program at the core of a computer's operating system with complete control over everything in the system.

**Virtual Machine**
- An abstraction of physical hardware turning one server into many servers. Each VM includes a copy of an operating system, the application, necessary binaries and libraries.

**Hypervisor**
-

**[Docker Engine](https://docs.docker.com/engine/)**
- Docker container runtime
- A client-server application with these major components
  - A server which is a type of long-running program called a daemon process
  - A REST API which specifies interfaces that programs can use to talk to the daemon and instruct it what to do
  - A command line interface client (the `docker` command)

docker daemon **server** which is surrounded by a REST API. The docker CLI **client** communicates with the REST API to control and interact with the Docker daemon. The daemon creates and manages Docker objects, such as images, containers, networks, and volumes.

**Docker CLI**
-

**UNIX sockets**

**Docker Hub**
- A public registry that anyone can use, and  Docker is configured to look for images on Docker Hub by default.

**Namespaces**
- Docker uses a technology called `namespaces` to provide the isolated workspace called the *container*. When you run a container, Docker creates a set of *namespaces* for that container.
- These namespaces provide a layer of isolation. Each aspect of a container runs in a separate namespace and its access is limited to that namespace.
- Docker Engine uses namespaces such as the follow on Linux:
  - `pid` namespace: Process isolation (PID: Process ID)
  - `net` namespace: Managing network interfaces (NET: Networking)
  - `ipc` namespace: Managing access to IPC resources (IPC: InterProcess Communication)
  - `mnt` namespace: Managing filesystem mount points (MNT: Mount)
  - `uts` namespace: Isolating kernel and version identifiers (UTS: Unix Timesharing System)

**Control groups**
- Docker Engine on Linux relies on another technology called *control groups*(`cgroups`). A cgroup limits an application to a specific set of resources. Control groups allow Docker Engine to share available hardware resources to containers and optionally enforce limits and constraints.

**Union file systems**

**Container format**
