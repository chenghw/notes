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

- Network Types in Docker
- Network Services
- Recap

***Docker Certified Associate Exam***
- Domain 4: Networking
  - Create a Docker bridge network for a developer to use for their containers
  - Publish a port so that an application is accessible externally
  - Identify which IP and port a container is externally accessible on
  - Describe the different types and use cases for the built-in network drivers
  - Deploy a service on a Docker overlay network

### Network Types

#### Bride Networking

Sometimes called *single-house networking*, it is the oldest and most common. 

Linux - bridge driver  
Windows - nat driver

**docker0** - default bridge network

Got a host running docker with a built in network called Bridge, or Nat on Windows. You can create more bridges, but each one of these is an island. Now we add containers, and each container gets an IP on that bridge network, and they can all talk to each other. However containers on separate bridges cannot without mapping of ports to the host.

#### Overlay Networking

Sometimes called *multi-host networks*. An overlay is a single lay-two network spanning multiple hosts. It doesn't matter if these are all on different networks. The overlay is encrypted by default. Overlay is for containers only, so no communicating to VMs etc.

`docker network create -o encrypted`
- `-o encrypted` - add encryption on the data plane

For overlay, really need Swarm mode active since the Overlay network leverages a bunch of security stuff in Swarm. It also being scoped to the Swarm means that its available to every node in the Swarm.

#### MACVLAN (transparent on windows)

This gives every container its own IP address and MAC address on the existing network. Meaning that containers can be viewed as first class citizens on your existing VLANs. No bridges or port mapping necessary. However this requires **promiscuous mode** on the host. Public cloud providers generally don't allow **promiscuous mode**. IPVLAN is an alternative that does not require **promiscuous mode** but it isn't fully backed yet.

#### Hands-on

`docker network ls` - list all networks

`docker network inspect bridge` - inspect the default bridge
- containers will get IPs from the IPAM.Config.Subet range

`docker port {CONTAINER}` - shows the port mappings

By default any containers added to the host will go on the default bridge unless told otherwise.

`docker network create -d {DRIVER NAME} {NETWORK NAME}` - create a new network
- `-d bridge` - driver options, for this case bridge network
- `-d overlay` - driver options, for this case overlay network

`docker container run --network {NETWORK NAME}` - run a container on a specific network

`docker service create -d --name pinger --replicas 2 --network overnet alpine sleep 1d`

### Network Services

Some built in network services include **Service Discovery** and **Load Balancing**.

**Service Discovery** is all about locating services in a swarm 
- every new service gets a name
- service names are registered with DNS on the swarm
- every service task gets a DNS resolver that forwards lookups to the swarm based DNS service
- all this equates to, all swarm services are pingable by name as long as you are pinging it from on the same network

**Load Balancing** let's us access a service from any node in the swarm, even nodes not hosting the service, and it balances load across them.
- ingress load balancing
  - thats where external clients can access a swarm service via any of the nodes in the swarm
  - this means that you can hit any node within a swarm even if it isn't running the service, and still hit the service

`docker service create -d --name web --network overnet --replicas 1 -p 8080:80` - the important part of this is the port mapping. The service is mapping its 80 to every swam nodes 8080, this is swarm wide.  

### Recap

Out of the box Docker provides a bunch of drivers for different use cases. The bridge driver's fine for development and really simple to use-cases. It's all single host and external access requires port mappings. Overlays are way better. This is proper multi-host networking. It creates a multi-host, layer two network so that containers on different hosts can easily join the same secure network, however it is container only. If we want to plug our containers into existing VLANs, we want to take a look at MACVLAN, or transparent on Windows. This gives every container its own MAC address and its own IP address on your existing VLAN but it requires promiscuous mode on the host adapter, which is not supported on most cloud platforms. There is also built-in network services. Service discovery makes every service on the swarm discoverable via a built-in swarm DNS. Load balancing makes it so that every node in the swarm knows about every service. It lets up point external load balancers to any or in fact every node in the swarm. No matter which node we hit, we reach the intended service. The stack is pluggable so you can plug in third-party drivers for things like IP address management and maybe different network topologies.

## Working with Volumes and Persistent Data

Volumes are a great way to store persistent data and they're nicely decoupled from containers.

- Big Picture
- Managing Volumes
- Working with Volumnes
- Recap

***Docker Certified Associate Exam***
- Domain 1: Orchestration
  - Mount volumes
- Domain 6: Storage and Volumes
  - Describe how volumes are used with Docker for persistent storage

### The Big Pciture

Every container gets its own non-persistent storage. Its free and comes with every container. Usually its on local block storage managed by the storage driver or the graph driver. Focus is on performance and does all the union file system for the container.

Persistent storage is called volume storage. We have to specifically create it and it lives outside of the graph driver away from all the union mount stuff. Generally speaking, a volume is a directory on the Docker host that's mounted straight into the container at a specific mount point. Behind the scenes it can be mounted else where, so long as that storage system has a Docker volume driver. Volumes live outside of the container space and has its own `docker volume` sub command, but it seemlessly plugs into containers and services. You can attach more than one container to a volume, however you need to be careful to avoid corruption.

### Managing Volumes

`docker volume` sub command with all the usual suspects `ls`, `create`, `inspect`, `rm`, etc.

### Attaching Volumes to Containers

`docker container run -dit --name voltest --mount source=ubervol,target=/vol alpine:latest`
- `--mount` - this flag is how we attach a volume to a container. If you specify a volume that doesn't exist, Docker is going to create one for you
  - `source` - the name of the volume, either fine or create it
  - `target` - where in the container to mount it

Linux - `/var/lib/docker/volumes/{NAME}/_data/` - where the data lives on the host

As long as a volume is in use by a container, it cannot be deleted.

### Recap

All containers get a local graph driver storage, it's what stacks the image layers and adds the writeable container layer but it's bound to the container. so you delete the container and the graph driver storage goes with it.

Graph drivers are being replaced in containerd with ***snapshotters***

For persistent data, we need volumes. Volumes operate outside of the graph driver and have a life cycle totally independent of the container. To manage volumes, you leverage the `docker volume` sub command. Thanks to the plugin architecture, volumes can exist not only on the local block storage of your Docker host, but also on high-end external systems as long as it's got a Docker storage driver.

## Working with Secrets

- Big Picture
- Secrets on the CLI
- Secrets in Apps
- Recap

***Docker Certified Associate Exam***
- Domain 1: Orchestration
  - Add networks, publish ports
- Domain 4: Networking
  - Deploy a service on a Docker overlay network
  - Publish a port so that an application is accessible externally

### The Big Picture

**Docker Secrets** is a text blob that is up to 500kb, half a mb.
- safe
- secure
- infrastructure independent
- Requires Swarm-mode (secrets come out of the box) and just for services
- Linux: Docker 1.13+
- Windows: Docker 17.06+

In Swarm mode we can create a secret, `docker secret create ...`. This gets sent to the manager over a secure connection, and the manager puts it in the store and is encrypted at rest. You can then explicitly gant a service access to a secret. `docker service create --name {NAME} --secret {SECRET} --replicas 2 ...`. After that, a manager then sends that secret over a secured connection to just the nodes in the swarm that are running a replica for the service. Least-priviledged model. Once inside the worker node, it gets mounted inside the service task in its unencrypted form. Linux: `/run/secrets/`, Windows: `C:\ProgramData\Docker\Secrets\`. For Linux, that folder is an temp FD volume, so an in-memory file system. At no point is the secret ever persisted to disk on the node. For Windows, there is no in-memory file system, so it does get persisted to disk on the node. So you may want to mount the Docker root directory using BitLocker or something. Once the service is removed or the secret is revoked, then the worker node is instructed to flush it from memory.

### Secrets on the CLI

`docker secret create wp-sec-v1 ./classified` - create a secret with the name `wp-sec-v1` and use the contents of the `./classified` file as the actual secret.

### Secrets in Apps

### Recap

**Docker Secrets** a safe a secure way to publish secrets to applications, and it is platform-independent. First we create the secret, which in the Docker world is a half a mb in size. When we create it, it gets sent to the Swarm over a secured network connection. It is then placed in the raft where it's encrypted at rest. Then we create or update a service, and as apart of this operation we authorize the service to access the secret, which causes the control plane to send it to the nodes in the swarm that are running replicas for that service. Again, it's encrypted in flight. When it hits the node, it is never persisted to disk (except for Windows). Instead it gets mounted into the service replica, which is a container, as a file in an in-memory file system, and it is unencrypted at this point. Linux: `/run/secrets/`, Windows: `C:\ProgramData\Docker\Secrets\`. Once the service is done, the control plane tells the client on the worker nodes to flush it from the nodes. 

## Deploying in Production with Stacks and Services

- Big Picture
- Stack Files
- Deploy and Manage a Stack

***Docker Certified Associate Exam***
- Domain 1: Orchestration
  - Extend the instructions to run individual containers into running services under swarm
  - Convert an application deployment into a stack file using a YAML compose file with `docker stack deploy`
  - Manipulate a running stack of services
  - Increase number of replicas
  - Mount volumes

### The Big Picture

Most apps are a bunch of smaller services that work together. And in the Docker world, that's Services with a capital S. So the Service object in the Docker engine API. Manage of bunch of these Services for them to work together...enter stacks.

Stacks work on the Docker command line(CLI), the universal control plain(UCP) GUI, and Docker Cloud. 

Dev View:  
Our application code is split up, some may be written in different languages (Python, Node, Go, etc.). We make each one of these a container. For scalability and self-healing, we deploy them as (Docker)Services. Each service at this point is still deployed and managed separately, but that is not ideal. So we group them as a stack. The stack is the highest layer of the Docker application hierarchy. 

Ops View:  
We start with a stack and define a bunch of services, networks, and volumes. The services define containers. We deploy and manage stacks with the `docker stack` sub command. Or through Docker Cloud on the web UI. The stack file is basically a compose file. 

A stack file is a great way to define your app. A developer can create a stack file and hand it over to ops and ops will have a great description of the application. You can even fork your stack file for things like dev, test, and prod. 

### Stack Files

Stack files are pretty much compose files, but they need to call at least the v3 file format (`version: "3"`)

In the example below we are defining 6 services, 2 networks, and a volume. There are 4 top level keys, `version`, `services`, `networks`, and `volumes`. Each service is its own JSON dictionary with its own set of keys. Taking a look at the `redis` service, we have the `image` it is using, the `ports` its exposes, the `networks` its using, and the `deploy`. The `deploy` is the new stuff in v3. It's where we define all the swarm and the stack stuff. 1 replica (so one container), when we do rolling updates, we update 2 at a time (parrallelism) with a delay of 10s in-between each. And lastly a restart policy. 

Topology-aware scheduling
- Schedule based on node labels etc
Health-aware scheduling
- Only schedule to healthy nodes
H/A scheduling
- Spread replicas across multiple nodes

Example:
```yml
version: "3"
services:

  redis:
    image: redis:alpine
    networks:
      - frontend
    deploy:
      replicas: 1
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure
  db:
    image: postgres:9.4
    environment:
      POSTGRES_USER: "postgres"
      POSTGRES_PASSWORD: "postgres"
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - backend
    deploy:
      placement:
        constraints: [node.role == manager]
  vote:
    image: dockersamples/examplevotingapp_vote:before
    ports:
      - 5000:80
    networks:
      - frontend
    depends_on:
      - redis
    deploy:
      replicas: 2
      update_config:
        parallelism: 2
      restart_policy:
        condition: on-failure
  result:
    image: dockersamples/examplevotingapp_result:before
    ports:
      - 5001:80
    networks:
      - backend
    depends_on:
      - db
    deploy:
      replicas: 1
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure

  worker:
    image: dockersamples/examplevotingapp_worker
    networks:
      - frontend
      - backend
    depends_on:
      - db
      - redis
    deploy:
      mode: replicated
      replicas: 1
      labels: [APP=VOTING]
      restart_policy:
        condition: on-failure
        delay: 10s
        max_attempts: 3
        window: 120s
      placement:
        constraints: [node.role == manager]

  visualizer:
    image: dockersamples/visualizer:stable
    ports:
      - "8080:8080"
    stop_grace_period: 1m30s
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"
    deploy:
      placement:
        constraints: [node.role == manager]

networks:
  frontend:
  backend:

volumes:
  db-data:
```

### Deploy and Manage Stacks

`docker stack deploy -c stackfile.yml {NAME}`

`docker stack ps {NAME}`

`docker stack services {NAME}`

`docker service scale voter_vote=20` - increase number of replicas
- better way of doing this would be updating the stackfile.yml and redeploying the stackfile

### Recap

Apps are generally a collection of smaller services. In the Docker world, these apps are made up of containers that are run as services. We then group these services into stacks. A stack is a bunch of services that make up an app. We define it in a YAML file. Pretty much a compose file with a few extensions to deal with Swarm stuff. You do need to be using version 3 or later of the compose file spec. Even though it is using a compose file, you don't need to install compose as a separate tool. The stack stuff is baked directly into the engine. You must be in Swarm mode, because stacks are all about Swarm. You deploy with the `docker stack deploy` command. This does a few things, it records the desired state of the app on a cluster, and Raft will make sure every manager will have the latest copy of it. Secondly, it deploys the app which includes all the services, networks, volumes, etc. Thanks to background reconciliation loops, swarm manages the app. Its got this notion of desired state, from the stackfile, of how the app should look on the cluster. If anything every changes, ie a node failing, then Swarm sees that the observed state of the cluster is no longer matching the desired state so it goes to work fixing it. We end up with a self-documented, reproducible app that fits nicely into version control.

## Enterprise Tooling

- Show some enterprise-grade value-add products
- Cover some DCA exam objectives

- Big Picture
- Docker Universal Control Plane (UCP)
- Docker Trusted Registry (DTR)
- RBAC
- Image Scanning
- Load Balancing
- Recap

***Docker Certified Associate Exam***
- Domain 2: Image Creation, Management, and Registry
  - Deploy a Registry
  - Configure a Registry
  - Login to a registry
  - Push an image to a registry
- Domain 3: Installation and Configuration
  - Consistently repeat steps to deploy Docker engine, UCP, and DTR on AWS and on premises in an HA config
- Domain 4: Networking
  - Use Docker to load balance HTTP/HTTPS traffic to an application (Configure L7 load balancing with Docker EE)
- Domain 5: Security
  - Demonstrate that an image passes a security scan
  - Configure RBAC in UCP
  - Describe the difference between UCP workers and managers

### The Big Picture

### Docker Universal Control Plane (UCP)

### Docker Trusted Registry (DTR)

### Role-based Access Control (RBAC)

### Image Scanning

### Layer 7 Load Balancing

### Recap

## What Next

- [Meetups](https://events.docker.com/chapters/)
- [DockerCon](https://dockercon.com)
- [Docker Certified Associate Exam](https://success.docker.com/certification)
- [Kubernetes](https://kubernetes.io/)
