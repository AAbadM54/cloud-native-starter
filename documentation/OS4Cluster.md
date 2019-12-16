# Get Access to an OpenShift Cluster

At the time of this writing, OpenShift 4.2 is the latest release of Red Hat's OpenShift Kubernetes distribution. This is a Red Hat licensed product, it is not available as free download anywhere.

Here are two options to get access to OpenShift:

## Option 1: CloudReady Containers (CRC)

There is a method that allows to run OpenShift 4.2 on your own laptop without purchasing an OpenShift license. It is called CodeReady Containers (`crc`) and is the sequel to MiniShift.

`crc` is available for Mac, Linux, and Windows 10, and it uses native virtualization to run OpenShift, namely Hyperkit for Mac, Hyper-V for Windows, and KVM for Linux. 

Red Hat only supports Windows 10 Pro and Home with Fall Creator's Update, and on the Linux side `crc`is supported on Red Hat Enterprise Linux, CentOS, and Fedora. Ubuntu and other Linux users currently may have to perform additional configuration on their machines. This is due to different approaches in network name resulution (dnsmasq vs systemd-resolved). You can find information on [CRC Github Issues](https://github.com/code-ready/crc/issues). Hopefully this will be fixed in the future.

To install `crc` follow the official documentation [here](https://cloud.redhat.com/openshift/install/crc/installer-provisioned). You need a Red Hat account to access this page, and you can register for an account on this page if you do not already have one. Its free and you won't be able to install `crc` without an account.

When you sign on to this page, you get access to the official documentation, the download links for the `crc` binaries, and to the 'Pull Secret' and this is the reason to go through this page because the Pull Secret is personalized to your Red Hat account.

Once you obtained the binary for your operating system and your pull secret, you can follow the `crc` [Getting Started Guide](https://code-ready.github.io/crc/) or a blog I wrote about running [Red Hat OpenShift 4 on your laptop](https://haralduebele.blog/2019/09/13/red-hat-openshift-4-on-your-laptop/). 

The default setup of `crc` has these (steep) hardware requirements:

* 4 virtual CPUs
* 8 GB RAM
* 35 GB disk space for the virtual disk

On current Intel CPUs (i5, i7), 2 virtual CPUs are equal to 1 physical core. 
In my opinion, 8 GB of RAM are not sufficient especially when you install Istio which we will do. 16 GB of RAM will work. 
Be aware that you still need resources not reserved by `crc` to run the OS of your workstation. That means you won't be able to use `crc` on a smaller (older) machine! 
Also note that you cannot change the configuration (CPUs, RAM, disk space) once you started `crc` for the first time. You will need to delete and restart it to create the VM running the OpenShift cluster with new specifications.

The binaries are updated about once per month and previous to `crc` version 1.2 you had to delete and reinstall your cluster every month due to invalid certificates. Starting with version 1.2 `crc` can update the certificates and keep running. You may still want to update it from time to time since newer versions of `crc` contain newer versions of OpenShift. Update means delete your cluster and start fresh with a newer version of `crc`!

## Option 2: Red Hat OpenShift on IBM Cloud

I will update this section as soon as it is available (as public beta) to everyone.



**Continue** with [Installing Istio aka Service Mesh on your OpenShift cluster](OS4ServiceMesh.md)