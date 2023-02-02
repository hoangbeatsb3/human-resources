
<div align="center">

<h3 align="center">Human Resources Project</h3>

  <p align="center">
    An awesome tool to manage human resources
    <br />
    <br />
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Human Resources is a project that help admin/manager manage employee hierarchy easily.

Features:
* Create employee hierarchy.
* Retrieve the hierarchy employee.
* Retrieve specific supervisor of employee

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With


* Java 1.8
* Spring
* SQLite
* Docker
* Lombok

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started


### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* Java 1.8+
* Spring 2.3.3.RELEASE
* Lombok 1.18.20


### Installation

Clone the repo
   ```sh
   git clone https://github.com/hoangbeatsb3/human-resources.git
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Build the project
   ```sh
   mvn clean package
   ```
Build docker
   ```sh
   docker build -t human-resources .
   ```
Run docker container
   ```sh
   docker run -d --name human-resources -p 8080:8080 human-resources
   ```

APIs
* Create employee hierarchy:
  * POST http://127.0.01/users
* Get employee hierarchy:
  * GET http://127.0.01/users
* Get hierarchy by employee name:
  * http://127.0.01/users?name=XX

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

Hoang Nguyen - hoangbeatsb3@gmail.com

Project Link: [https://github.com/hoangbeatsb3/human-resources](https://github.com/hoangbeatsb3/human-resources)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

