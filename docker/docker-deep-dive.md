# Docker Deep Dive

https://app.pluralsight.com/library/courses/docker-deep-dive-update/table-of-contents

Path - Container Management using Docker

## Course Overview

- Docker
- Kubernetes

The fundamentals
- Namespaces
- Control groups
- Union file systems

Linux and Windows

Images, containers, swarms, services, secrets, stacks, and more

Hands on labs and exercises

Should provide all the tools and knowledge to pass the Docker certified associates exam.

## Course Intro

**Outline**

- Installing Docker
- Docker Architecture & Theory
- Working with Images
- Building New Images
- Working with Containers
- Building a Secure Swarm
- Container Networking
- Working with Volumes and Persistent Data
- Working with Secrets
- Deploying in Production with Stacks and Services
- Enterprise Tooling
- What Next

## Installing Docker

### [Play with Docker](https://labs.play-with-docker.com/)

A free Docker playground. (4 hours of playtime with a set Docker version)

Add new instance creates a entirely new Docker node which can communicate with other nodes, be clustered, etc.

Docker for Mac is running a Linux VM so they are running Linux containers.

Docker for Windows can run both Linux and Windows containers.

## Architecture and Theory

- Big picture view
- Kernel primitives
- Docker Engine

***Docker Certified Associate Exam***
- Domain 3: Installation and Configuration
  - Demonstrate the ability to upgrade the Docker engine
  - Understand namespaces and cgroups

### Architecture Big Picture

- A container is a ringed fenced area of an operating system, with some limits on how much system resource it can use.
  - Isolated area of an OS with resource usage limits applied

           Controls Groups
    - - - - - -    - - - - - - -
    | Resource |    | Resource |
    - - - - - -    - - - - - - -
         |               |
- - - - - - - - - - - - - - - - - -
|        |               |        |
|  - - - - - - -   - - - - - - -  |
|  | Isolation |   | Isolation |--|--- Namespaces
|  - - - - - - -   - - - - - - -  |
|                                 |
|        Operating System         |
- - - - - - - - - - - - - - - - - -

Docker engine interacts with Control Groups and Namespaces which are hard to manage at the kernel level.

CLI >>> API >>> Docker Engine (daemon, containerd, OCI)

CLI - `docker container run...`

CLI calls to the API `/container/create`

Docker Engine takes that request, gathers all the necessary kernel stuff and outputs a container.

### Kernel Internals

Container primitives in the kernel.

Both Namespaces and Control Groups are Linux kernel primitives (there are equivalences on Windows).

**Namespaces**
- are about isolation.
- allows us to take an operating system and carve it into multiple, isolated, virtual operating systems.
- Multiple containers are sharing a single kernel on the host
- Linux namespaces: Process ID (pid), Network (net), Filesystem/mount (mnt), Inter-poc comms (ipc), UTS (uts), User (user - mapping container user back to a user on the host)
- A Docker container is an organized collection of Namespaces. This collection is the Docker container's namespaces so it has its own pid, net, mnt, ipc, uts, user

**Control Groups**
- are about grouping objects and setting limits. Overall idea is to group processes and impose limits.
- **cgroups** (Windows a.k.a. Job Objects)
- Responsible for allocating how much CPU, RAM, and disk space to each container

**Union fs/mount & CoW**

### Docker Engine

The Decker engine exposes an API to us the user and under the hood it interfaces with all the kernel magic, and out pops containers.

Many things plug into the Docker. Examples: Docker Swarms, Docker Registry, Rancher, DataDog, CircleCI, etc.

#### History

Docker came out of a company called dotCloud. In the beginning, it was a Python tool called dc. It was basically a wrapper for LXC and AUFS. AUFS is a union file system and LXC is a bunch of tools for interfacing with the container primitives in the kernel.

Docker used LXC until libcontainer. This allowed Docker to control one of the key pieces to their architecture. Eventually dc turned into docker and Docker soon became a monolith (HTTP srvr, exec, images, compose, authz, runtime, builds, vols, orchestration, network, registry, REST API, libcontainer). Now, we call this the daemon. With all of this Docker became bloated and slow. Docker then went on an effort to break up their monolith into separate tools, which around the same time the Open Container Initiative (OCI) came out with some standards (Image spec, Runtime spec).

Fast forward to now. Docker client which communicates through a REST Docker API to the daemon which communicates with containerd, which handles execution and lifecycle operations (start, stop, pause, unpause). OCI layer (runtime) which interfaces with the kernel.

Linux - The client asks the daemon for a new container. The daemon gets containerd to start and manage the containers, and runc at the OCI layer, actually builds them. runc is the reference implementation of OCI runtime spec. runc is the default runtime for a vanilla installation of Docker.

Windows - In terms of day to day user experience it is the same. There is still the Docker Client and Docker daemon, but instead of containerd and runc there is the Compute Services layer.

#### Creating a new container on Linux

1. `docker container run ...`
1. `POST` to `/vX.X/containers/create` HTTP/1.1 in the daemon
1. At this point the daemon does not know how to create the containers due to all the refactoring. This logic was ripped out and moved into the OCI layer. So in order to create the container, the daemon calls out to containerd over a GRPC API on a local Unix socket.
1. containerd (a daemon process, long running) does not actually create the container, the logic that deals with the kernel is in the OCI layer. containerd starts a shim process for every container which then runc creates the container and then exits. runc is called for every container but does not stick around, however the shim does.

All this decoupling of daemon, containerd, and runc, allows for re-starting of the daemon or containerd and not effect running containers.

Orchestration, builds, stacks, overlay-networks, etc are getting implemnted in the daemon.

containerd and GRPC are Cloud Native Computing Foundation projects.

### Windows Containers

Windows 10 and Server 2016

In order for Windows to be able to run containers, Microsoft had to implement a few main pieces into their kernel. Namespaces and control groups (Job Objects). Namespaces allowed windows to create multiple isolated user-spaces. They also did a bunch of work on NTSFS and the registry so that we can get image layering like we have with AUFS and overlay FS. They also ported the Docker client and daemon to windows.
docker.exe as the client and dockerd.exe as a Windows service, the daemon.

Unlike a Linux container where you'd see a single process, a Windows container will have many processes. Overtime Windows has developed a bunch of interdependencies, so apps need certain system services, DLLs, to be available, and in turn, some of those reply on others, and if they're not there, things break. Every container needs these processes.

There are two types of Windows containers: **Native Windows Containers** and **Hyper-V Containers**. Both are for win32 apps.

When you spin up a Hyper-V Container, Windows spins up a lightweight Hyper-V VM in the background. Inside this VM it has its own OS and its own kernel. The container is then run on that kernel instead of the host kernel. For Hyper-V Containers, its always one container per VM. `docker container run --isolation=hyperv`

### Recap

Namespaces, Control Groups, union filesystem. OCI (Open Container Initiative),

## Working with Images

- Big Picture
- Details
- Registries
- Best practices
- Recap

***Docker Certified Associate Exam***
- Domain 2: Image Creation, Management, and Registry
  - Use CLI commands such as list, delete, prune, rmi, etc to manage images
  - Inspect images and report specific attributes using filter and format
  - Push an image to a registry
  - Pull an image from a registry
  - Describe how image layers work
  - Display layers of a Docker image
  - Describe how image deletion works
  - Demonstrate tagging an image
  - Tag an image
  - Utilize a registry to store an image

### Images: The Big Picture

An image is a read-only template for creating application containers. Inside of it is all the code and supporting files to run an application. Images are build-time constructs while containers are their run-time siblings.

An image is just a bunch of files and a manifest.
- OS files & objects
- App files
- Manifest

An image is a bunch of layers stacked on top of each other.

Images are stored on a registry which can be cloud of on prem. And we pull them onto our hosts using `docker image pull ...` command. Once on our hosts, we can start containers form them. Each container that is created will have its own RW layer which will perform all writes/updates.

### Images in Detail

**Layers - Manifests - Hashes - Drivers**

`docker image pull redis`

Example output:
```
Using default tag: latest
latest: Pulling from library/redis
54fec2fa59d0: Pull complete
9c94e11103d9: Pull complete
04ab1bfc453f: Pull complete
5f71e6b94d83: Pull complete
2729a8234dd5: Pull complete
2683d7f17745: Pull complete
Digest: sha256:157a95b41b0dca8c308a33489dfdb28019e033110320414b4b16fad7d28c0f9f
Status: Downloaded newer image for redis:latest
docker.io/library/redis:latest
```

Each of the `Pull complete` is a layer.

An image is a bunch of independent layers that are very loosely connected by a manifest file (sometimes referred to as a config file). The manifest file describes the image, including the layers that get stacked and how to stack them. A layer in an image has no references to other layers within the image, if there are references that connect them that is done in the manifest.

The `docker image pull ...` command is generating an API request to the Docker registry API on a registry somewhere (by default Docker Hub).

The `pull` is a 2 step process.
1. Get the manifest
1. Pull the layers

The client will first look for something called a **Fat Manifest**, which is like a manifest of manifests. A list of architectures supported and a manifest for each of those. So for the example above of `docker image pull redis`, the Fat Manifest will have a list of systems/architectures that supports (OSType: linux, Architecture: x86_64) and the pull the image manifest for that architecture. Once we have the image manifest, we parse it for the list of layers and download them from the registries blob store.

In Docker 1.10, Docker added **content addressable storage**, which at a high level recognizes that each layer has a bunch of files inside. Then uses a hash of all that content and use it as the image ID. This allows us to ask for an image with a specific hash, download it, and hash it again to see if the two match.

Behind the scenes there is a storage driver pulling together all this layering.

How layering works. There is a base layer, which is typically the OS layer with all the OS Files & objects (root file system). On top of that layer can be your App Code, etc. Each of these layers are represented in a directory in the file system under /var/lib/docker/{STORAGE DRIVER NAME}

`docker history {IMAGE NAME}`
- History of how the image was built
- SIZE column - 0B generally commands that aded to the image config file, and the non-zeros are the added layers

`docker image inspect {IMAGE NAME}`
- shows their config and layers

`docker image rm {IMAGE NAME}`
- removes the image including all the files in the storage driver

### Registries

Images live in registries.

When you pull an image to your local host, the image gets pulled to a local registry.
- Linux:  /var/lib/docker/{storage-driver}
- Windows: C:\ProgramData\docker\windowsfilter

**Official Images and Unofficial Images**
- Official images live at the top level of the Hub namespace
  - `docker.io/redis` or `docker.io/nginx` >>> if going by defaults and pulling from Docker Hub, the `docker.io/` can be left off
- Unofficial images live beneath a username or organization

Registry structure - `Registry` / `Repository (Repo)` : `Image (Tag)`
Defaults - `docker.io` / {REPO} : `latest`

Repository management - pushing a new image does not automatically tag it as latest. This is a manual process.

Reminder - Docker leverages the images content hash to identify layers.

Locally we have the Image Config and the Content hashes of the layers. However we compress this when we send it over to the registry. The compression causes the content to change which in turn causes the content hashes to change. So to get around this, when we build the manifest that we push to the registry, we populate it with new hashes of the compressed layers. These new hashes are called `Distribution Hashes(or Digests)`.

https://docs.docker.com/docker-hub/official_repos/
https://github.com/docker-library/official-images

Official images should be safe and contain best practices. When looking how to build your own dockerfiles, these official images are good sources to learn from.

### Best Practices

- where possible use official images
- keep your images small - smaller attack surface with fewer vectors
- take official images and build on top of it
- be explicit when referring to images

### Recap

An image is a template for starting containers. It's read-only and we build it by stacking layers and having the storage driver make it look like a normal file style. It's not a monolithic blob, its a config file plus a bunch of independent layers. And it's inside these layers where all the application binaries, files, libraries and stuff, where they all live. And the config file has the instructions on how to run it as a container. So, how to set the env, which ports to expose, and how to start the packaged app. You can start multiple containers per image. Each container gets its own thin writable layer where it stores changes and each one of those can be linked back to a single image. But at no point does any of the data layers get changed. They are read-only. If a container wants to change a file in one of them, there's a copy on write operation, where it finds the image from whatever layer it's in, copies it up to its container layer, and makes the change there. Images live in registries which can be cloud or on prem. When we pull an image to our host, it lives in the local registry. With the content addressable storage model, this allows us to run a cryptographic algorithm over the contents of a layer and use the resulting hash as the layer's ID. The Image ID is a hash of the image config file. When we push images to registries, we compress them and compressing means changing the content. So, we needed a second hash for use with the registry. On Docker Hub, repos are split into Official and Unofficial. Official are safest and contain best practices.

## Containerizing an App

- Big picture
- Containerizing an App
- Digging deeper
- Multi-stage builds
- Recap

***Docker Certified Associate Exam***
- Domain 2: Image Creation, Management, and Registry
  - Show the main parts of a Dockerfile
  - Describe Dockerfile options [add, copy, volumes, expose, entrypoint, etc]
  - Give examples on how to create an efficient image via a Dockerfile
  - Apply a file to create a Docker image

### The Big Picture

It all starts with App code and in the end a running App. In order to do this we construct a **Dockerfile**.

**Dockerfile** - a list of instructions on how to build an image with our code inside

Once we've constructed out Dockerfile, we use `docker image build` to build our image. From the image, we then build a container from that.

### Containerizing an App

Best practice to put the `Dockerfile` in the root folder of the application. The file name is opinionated so yes `Dockerfile` with a capital `D`.

The app code will need something to run on so depending on the OS we'll need some form of that OS's base image (linux - alpine, unbutu, etc). We start our Dockerfile from there. A Dockerfile is really just a list of key value pairs, instruction and then value.

Docker host must be running the right platform and architecture as the Dockerfile.

```Dockerfile
FROM alpine

LABEL maintainer="ho.won.cheng@gmail.com"

# apk is a package manager for alpine
# apt would be the ubuntu equivalent
RUN apk add --update nodejs nodejs-npm

COPY . /src

# metadata for the config
WORKDIR /src

RUN npm install

# metadata for the config
EXPOSE 8080

# metadata for the config
ENTRYPOINT ["node", "./app.js"]
```

**Dockerfile notes**
- Instructions for building images
- CAPITALIZE instructions
- {INSTRUCTION} {value}
- `FROM` always first instruction
- `FROM` = base image
- Good practice to list maintainer
- RUN = execute command and create layer
- COPY = copy code into image as new layer
- Some instructions add metadata instead of layers
- ENTRYPOINT = default app for image/container

`docker image build -t {TAG NAME} .`
- the `.` says to use the current directory as the context

Once built we can `docker image ls` to see the newly built image.

`docker container run -d --name {NAME} -p 8080:8080 {TAG NAME}`
- `-d` - detached so it doesn't steal the terminal
- `--name` - a name to refer back to container later
- `-p` - mapping ports from host to container `{HOST PORT}:{CONTAINER PORT}`

### Digging Deeper

**Dockerfile notes**
- Text instructions for building images
- Read by `docker image build` command
- Read from the top, one instruction at a time
- Write instructions UPPERCASE
- FROM = first instruction & base image/layer
- Good idea to start from official image
- RUN = executes commands and creates layers
- COPY = copy code into image as new layer
- Some instructions add metadata instead of layers

**Build context**
- Location of your (source)code.
- *"My Dockerfile is located in the build context"*

Everything in your build context gets sent to the Daemon.

Docker Client ---Send context---> Docker Daemon

This can all be on the same host or remotely. The build context can even be a git repository. `docker image build -t {TAG NAME} https://{GIT REPOSITORY}`

Once you build an image you can see what is happening at each step. Some steps will create temporary container, output a layer, and remove itself. Each step is creating a layer. However some layers are just metadata and in the end are not included, If we do a `docker image inspect {NAME}`, those metadata layers are not included in the end.

### Multi-stage Builds

Size matters. Smaller is better. Faster builds, faster deployments, less money wasted on storage, and less attack surface. Minimal OS and packages is the gold standard.

A Dockerfile with multiple `FROM` instructions. Each of these FROMs marks a distinct build stage.

Example Dockerfile:
```Dockerfile
# AS will alias this stage with storefront
FROM node:latest AS storefront
WORKDIR /usr/src/atsea/app/react-app
COPY react-app .
RUN npm install
RUN npm run build

# AS will alias this stage with appserver
FROM maven:latest AS appserver
WORKDIR /usr/src/atsea
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package -DskipTests

# AS will alias this stage with production
FROM java:8-jdk-alpine AS production
RUN adduser -Dh /home/gordon gordon
WORKDIR /static
# takes just the built code from storefront and leaves the rest behind
COPY --from=storefront /usr/src/atsea/app/react-app/build/ .
WORKDIR /app
# takes just the built code from appserver and leaves the rest behind
COPY --from=appserver /usr/src/atsea/target/AtSea-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "/app/AtSea-0.0.1-SNAPSHOT.jar"]
CMD ["--spring.profiles.active=postgres"]
```

### Recap

Start off with App code, then we add a Dockerfile which is a list of instructions that tell how to take our code and make an image from it. Start with a base image and start adding our app on top. Add some metadata like networking config etc and we can then `docker image build` and then we have an image ready to build containers. Next step, we have multistage builds which gives us leaner and better images ideally suited for production environments.

A Dockerfile is living documentation that bridge the gap between dev and ops. If one understands a Dockerfile, this can help onboard new people to get up and running as well as understand the application.

## Working with Containers

- Big Picture
- Deeper Dive
- Logging
- Recap

***Docker Certified Associate Exam***
- Domain 1: Orchestration
  - Inspect images and report specific attributes using filter and format
  - Add networks, publishing ports
- Domain 2: Image Creation, Management, and Registry
  - Interpret the output of `docker inspect` commands
- Domain 4: Networking
  - Publish a port so that an application is accessible externally
  - Identify which IP and port a container is externally accessible on

### Containers: The Big Picture

In the Docker world, the most atomic unit of scheduling is the container. It's the smallest unit of work we can create. In the virtualization world thats a VM. In the Kubernetes world, it's a pod.

Containers are running instances of images. Images are build-time and Containers are run-time. An image is read-only. All a container is, is a thin writeable layer on top of the read-only image.

If a container wants to edit a file in the image, it can't since the image is read-only. So instead of editing the file within the image, the container locates it in the image layers, makes a copy of it in its own writeable layer, and makes the changes there. That way, the container gets the full read-write experience, but without having to have write access to the image. This is called, **copy on write**.

Switching context to an OS perspective. A container is its own isolated pod within an OS. Not too different from VMs on Hypervisor, but instead of virtualizing hardware resources, containers are virtualizing operating system resources (file systems, process trees, network stacks, etc.)

Container Lifecycle - Start, Stop, Pause, Unpause, Delete. Containers are persistent. So stopping or pausing a container does not get rid of any of the data inside it. Everything sticks around until the container is deleted.

Containers are all about apps. And the so called gold standard when it comes to containerized apps is microservices. This is where each container generally runs a single process and has a single job.
Example:
Imagine a monolith where a single container includes `Web FE`, `Cart`, `Tracking`, `Inventory`, `Shipping`. We break each function out into its own container and then we glue everything together with APIs. But to be clear containers are not just for microservices.

**Modernize Traditional Apps**
1. Existing Application
1. Convert to a Container with Docker EE
1. Modern Infrastructure - Built on premise, in the cloud, or as part of a hybrid environment
1. Modern Methodologies - Integrate to CI/CD and other automation systems
1. Modern Microservices - Add new services or start peeling off services from monolith code base

The ideal for a container is that they should be treated as **ephemeral**(short lived) and **immutable**(we really don't want to be logging into them, poking around, making changes. If we want to make changes, updates are made to the image and new containers are built).

### Diving Deeper

- `docker container ls` - list containers
- `docker container run -it alpine sh` - run container
  -  `-it` - interactive terminal
  - `alpine` - image to be used
  - `sh` - shell command
  - in this instance the shell is the application. Exiting from the application will remove the container.
- `ctrl+P+Q` - to get out of the container without exiting the application, thus leaving the container still running
- `docker container stop {ID}` - the ID only needs to be filled out up until uniqueness. The container will be given 10 seconds to sort it's stuff out. Stopping the container sends a signal to the main process in the container, PID1. If the container knows what to do with that signal, then it'll respond back to Docker, but if it doesn't Docker will give it 10 seconds before forcing it to stop.
- `docker container ls -a` - list all containers, including stopped containers
- `docker container exec -it {ID} sh` - enter into a container
  - `exec` - allows to enter into a container with the given command. This will start an new process in the container
- `docker container rm $(docker container ls -aq) -f` - removes all docker containers from the output of the `$(...)` with the force flag
  - `rm` - remove all containers
  - `$(docker container ls -aq)` - output of list of all containers
  - `-f` - force

Sometimes you can start a container without passing a process to run at the end of the command. This is because every image has a default process that it'll run if no process is passed to it. `docker container run -it alpine` will still run a shell process because the default is `/bin/sh`

If you `docker image inspect {IMAGE}`, you can see under the `"Cmd"` key what command will run by default.

**Default processes for new containers**
- **CMD**: Run-time arguments override CMD instructions
- **ENTRYPOINT**: Run-time arguements are appended to ENTRYPOINT

- `docker port {CONTAINER NAME}` - will give a list of the port mappings for this container to the host

### Logging

We are interested in two types of logs, **daemon logs** and **container logs**.

**daemon logs**
- are the logs from the Docker Engine/daemon.
- Linux
  - Modern Linux system uses `systemd`
  - `systemd` instances, those logs get send to `journald`
    - `journald` can be read by a `journalctl` command `journalctl -u docker.service`
  - `non-systemd`
    - Try `/var/log/messages`
- Windows
  - `~/AppData/Local/Docker`
  - Windows event viewer

**container logs**
- Docker's hoping that apps log to standard out (stdout) and standard error (stderr)
  - All processes outputs are being captured and forwarded.
  - Ideally have your app on PID1 logging out to stdout and stderr
- Since 17.05 or 17.06 (enterprise), Docker supports logging drivers.
  - plugins that integrate container logging with existing logging solutions (Syslog, Gelf, Splunk, etc.). Basic idea is take your container logs and forward them to whatever logging solution that is already in place. Most logging tools have a Docker driver.
  - Set default logging driver in `daemon.json` config file
    - After which any new containers will start using that driver
  - Override defaults of the host with `--log-driver --log-opts` when starting a container
  - By default, most Docker hosts default to JSON file logging. This writes out logs to a simple JSON file format.
    - Inspect logs with `docker logs {CONTAINER}` command to view them
      - Doesn't work with all drivers

### Recap

Containers are run-time cousins of images. Under the hood, they're an isolated application execution environment. It's a bunch of grouped namespaces that look and feel like a standalone OS. But containers are OS specific. There's no running Linux containers on Windows and vice versa, at least not without some magic going on behind the scenes. Containers don't contain a kernel, they have to talk to the kernel of the host and Linux containers need a Linux kernel Windows need a Windows kernel. We also covered that containers being a thin writable layer that gets latched on top of a read-only image using a combo of union mounts and a bit of copy on write. Containers should be ephemeral and immutable. Ephemeral being short lived and immutable meaning, we don't really want to be logging onto them and messing around. The idea is once they're deployed we should leave them alone. If we need to do a fix, build a new image and deploy a new version. The main process inside a container is usually going to be your application. Killing that process will kill the container as well. For logging its all about the main process stdout and stderr.

## Building a Secure Swarm

- Big Picture
- Deeper Dive
- Build a Secure Swarm
- Orchestration
- Recap

***Docker Certified Associate Exam***
- Domain 1: Orchestration
  - Complete the setup of a swarm mode cluster, with managers and worker nodes
  - Demonstrate steps to lock a swarm cluster
  - Paraphrase the importance of quorum in a swam cluster
- Domain 5: Security
  - Describe Mutual TLS

### The Big Picture

**Docker Swarm** vs **Kubernetes(K8)**

**Docker Swarm**
- secure cluster
- orchestrator
- native support for K8 (for the orchestrator parts)

What is a **Secure Swarm Cluster**?
- it's a cluster of Docker nodes
  - nodes could be managers or workers
- there is **Mutual TLS** where workers and managers mutually authenticate each other, and all of the network chat is encrypted.
- cluster stores are encrypted and get automatically distributed to all managers
- labels to tag nodes and customize the cluster
- once we got the cluster we can start scheduling containers to it
  - instead of individually running `docker container run` commands against specific nodes, and every time having to think about which node we should be running them on, instead, we throw commands at the cluster, and we let Swarm decide. Swarm does all the workload balancing, etc.
  - we can run two types of work on the cluster: **Native Swarm work** and **Kubernetes**
    - TODO: Check...but as of this course Kubernetes only runs on Docker Enterprise edition

### Swarm Clustering Deep Dive

Docker is a set of nicely packages tools. If you pop the hood, you're going to see a bunch of smaller tools (containerd, SwarmKit). Docker just bundles them, and wraps them in an API. So stuff like the Mobi engine and containerd, runC and SwarmKit, they're all separate tools. But bundled and slightly integrated for that Docker experience.

**SwarmKit**

[GitHub](https://github.com/docker/swarmkit)

SwarmKit powers Swarm mode.

#### History

Back in the day Docker had this orchestration piece called Swarm. You'd install Docker, then you'd lay a Swarm on top. It was okay, as long as you didn't mind frying your brain configuring it. The thing is though, it kind of led to a separate toolkit called SwarmKit. The idea being to build an open source all of the smaller component tooling so that people can pick and choose what they want. In Docker v1.12, SwarmKit got integrated into the overall Docker package. So since v1.12, Docker has this notion of Single-engine mode and Swarm mode. Single-engine is where you install individual Docker instances, and you work with them all separately. Swarm mode, though, that's where you bring them all together as a cluster. And like we've seen, we can start throwing work at the cluster, instead of having to hand pick a node for each and every container. Any node running as apart of a Swarm cluster is in what we call a Swarm mode. Not in a cluster: Single-engine mode.

#### Building a cluster

We've got a node here, Docker installed. `docker swarm init` - now we've got a Swarm. That node is now flipped into Swarm mode. Behind the scenes, a lot of magic just happened. This first node is now the first manager of the Swarm. The first manager of a swarm is automatically elected as the leader. As the leader, it's the root Certificate Authority(CA) of the Swarm. You can configure external-cas by passing in the `--external-ca` flag. The leader issues itself a client certificate (with a default certificate rotation policy), builds a secure cluster store which will be distributed to every other manager of the swarm (and it's encrypted). It also created a set of cryptographic join tokens. One for joining new managers, and another for joining new workers.

Example information in a client certificate
```
Name: node1
ID: cmof7db...
Swarm: 2a25c...
Role: manager
Expires: 2018...
```

#### Joining a cluster as a Manager

We take a node running Docker in Single-engine mode, and we run a `docker swarm join {CRYPTO JOIN TOKEN FOR MANAGERS}` command on it. Its then apart of the Swarm operating in Swarm mode and issued its own client certificate.

Swarm managers are configured for high availability. Any of these managers can fail, however the cluster keeps going. Behind the scenes, only one of them is truly active and that's the leader. Every Swarm has a single leader manager, every other manager is called a follower (or Reachable) manager.

You can issue commands to any of the managers, but its just going to proxy commands to the leader. If the leader fails, then we have an election, and one of the followers gets elected as a new leader. This is all handled by Raft. All the distributed consensus stuff is done by [Raft](https://raft.github.io/) (same for K8). The Raft Consensus Algorithm is the go-to solution for distributed consensus. The ideal number of managers is 3, 5, or 7. Any more than 7, more time will be spent on thinking about decisions rather than acting on them. Just make sure its an odd number, that will increase the chances of achieving quorum, and therefore avoiding split brain (a network partition of something where you end up with an equal number of nodes on each side). Raft is not a fan of unreliable networks, so connect your managers over decent reliable networks.

#### Joining a cluster as a Worker

`docker swarm join {CRYPTO JOIN TOKEN FOR WORKERS}`

Workers can be a mix of Linux and Widows. When a Worker joins, the Worker does not get access to the cluster store. The Worker does get a list of IPs for all the Managers. So if one Manager dies, the Workers can talk to the others. Workers also get their own client certificates. Workers do all the application work, which is either Native Swarm work or K8.

### Building a Secure Swarm

**Create a Swarm**
- Create a Swarm manager
  - Assign it a crypto ID
  - Elect it as the Swarm leader
- Create a Swarm config DB
  - Encrypt it
  - Configure it to automatically replicate with all Swarm managers
- Create a Swarm join token for new workers
- Create a Swarm join token for new managers
- Configure a new Root CA on the leader
  - Configure a 90 day certificate rotation period

All this is done with a single command `docker swarm init`

`docker system info` - system information including what mode its in.

`docker node ls` - list docker nodes within a swarm

`docker swarm join-token manager` - get the Manager join token

`docker swarm join-token worker` - get the Worker join token

Workers cannot query the cluster store, so `docker node ls` will fail. However on a manager this will display all nodes within the cluster.

Lets say a worker token has been compromised. On manager node `docker swarm join-token --rotate worker` will create a new token for future joins. Existing worker node membership is unaffected.

In order to look at the client certifiaces, the following command can be run: `sudo openssl x509 -in /var/lib/docker/swarm/certificates/swarm-node.crt -text`
In the Subject of that certificate, you'll find the organization(O) which is the swarm ID, the organizational unit(OU) which is the node's role, and the canonical name(CN) which is the cryptograph node ID.

Manager and Worker Tokens start with an identifier of `SWMTKN`, exmaple token:
- `SWMTKN-1-3jxvdt8j...r3ispd5p-1gtve8fo...eukpi29`
- `{IDENTIFIER}-1-{HASH OF THE CLUSTER CERTIFICATE}-{THIS PART DETERMINES IF ITS A WORKER OR MANAGER - this part changes when you rotate tokens}`

Restoring manager or restarting a backup can cause a couple of concerns. Docker gives you an option to lock a Swarm. This is called Autolock. It will prevent by default unless the unlock key is presented first.

**Lock your Swarm with Autolock**
- Prevents restarted Managers from automatically re-joining the Swarm
- Prevents accidentally restoring old copies of the Swarm
- Docker does not Autolock Swarms
  - Autolock new Swarm
    - `docker swarm init --autolock`
  - Autolock existing Swarm
    - `docker swarm update --autolock=true`

To update your certificate expiry time: `docker swarm update --cert-expiry 48h`

### Orchestration

High level on Orchestration. Doing anything at scale demands automation. There's no way you can individually manage tens or hundreds of nodes and hundreds or thousands of containers. You are going to need something that will self-heal, do smooth updates, rollbacks, etc. Swarm and Kubernetes do all of that. So in the Docker world, we start out with a Swarm cluster. A bunch of Docker nodes clustered together as a kind of pool. Once we've got this, we can deploy applications onto it, and we can be declarative of it. Here cluster, run my application, and can you be sure that there are always 4 containers supporting the web front end. It deploys the app and records the desired state of four web front end containers. But things break, so Swarm and K8 are constantly observing the cluster and any time the actual state diverges from the desired state, Swarm and K8 step in and self heal.

### Recap

Swarm is integral to the future of Docker. But there's two main aspects to it. There's the Secure Clustering, and then there's the Orchestration. On the clustering side, we add nodes as managers and workers. Managers are in charge of the cluster, and they make all of the Control Plane decisions. Workers, they do all of the application leg work. Great, well we build a cluster with a simple `docker swarm init`, and thrown in for free we get a boat load of security stuff. In Swarm all of this is configured out of the box, a root Certificate Authority (CA), Mutual TLS, an encrypted store, certificate rotation, and secure join tokens. Adding more nodes is as simple as `docker swarm join`. Once we got the cluster, we are ready for roll with app deployments. In that space we can go with Native Swarm or K8.

## Container Networking

### Network Types

### Network Services

### Recap

## Working with Volumes and Persistent Data

## Working with Secrets

## Deploying in Production with Stacks and Services

## Enterprise Tooling

## What Next
