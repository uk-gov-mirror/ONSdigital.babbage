job "babbage" {
  datacenters = ["eu-west-1"]
  region      = "eu"
  type        = "service"

  constraint {
    distinct_hosts = true
  }

  update {
    stagger      = "90s"
    max_parallel = 1
  }

  group "web" {
    count = 2

    constraint {
      attribute = "${node.class}"
      value     = "web"
    }

    task "babbage" {
      driver = "docker"

      artifact {
        source = "s3::https://s3-eu-west-1.amazonaws.com/{{DEPLOYMENT_BUCKET}}/babbage/{{REVISION}}.tar.gz"
      }

      config {
        command = "${NOMAD_TASK_DIR}/start-task"

        args = [
          "java",
          "-Xmx2048m",
          "-cp",
          "/usr/src/target/dependency/*:/usr/src/target/classes/",
          "-Drestolino.files=target/web",
          "-Drestolino.classes=target/classes",
          "-Drestolino.packageprefix=com.github.onsdigital.babbage.api",
          "com.github.davidcarboni.restolino.Main",
        ]

        image = "{{ECR_URL}}/babbage:concourse-{{REVISION}}"

        port_map {
          http = 8080
        }
      }

      service {
        name = "babbage"
        port = "http"
        tags = ["web"]
      }

      resources {
        cpu    = 1500
        memory = 2048

        network {
          port "http" {}
        }
      }

      template {
        source      = "${NOMAD_TASK_DIR}/vars-template"
        destination = "${NOMAD_TASK_DIR}/vars"
      }

      vault {
        policies = ["babbage"]
      }
    }
  }

  group "publising" {
    count = 1

    constraint {
      attribute = "${node.class}"
      value     = "publishing"
    }

    task "babbage" {
      driver = "docker"

      artifact {
        source = "s3::https://s3-eu-west-1.amazonaws.com/{{DEPLOYMENT_BUCKET}}/babbage/{{REVISION}}.tar.gz"
      }

      config {
        command = "${NOMAD_TASK_DIR}/start-task"

        args = [
          "java",
          "-Xmx2048m",
          "-cp",
          "/usr/src/target/dependency/*:/usr/src/target/classes/",
          "-Drestolino.files=target/web",
          "-Drestolino.classes=target/classes",
          "-Drestolino.packageprefix=com.github.onsdigital.babbage.api",
          "com.github.davidcarboni.restolino.Main",
        ]

        image = "{{ECR_URL}}/babbage:concourse-{{REVISION}}"

        port_map {
          http = 8080
        }
      }

      service {
        name = "babbage"
        port = "http"
        tags = ["publishing"]
      }

      resources {
        cpu    = 1000
        memory = 2048

        network {
          port "http" {}
        }
      }

      template {
        source      = "${NOMAD_TASK_DIR}/vars-template"
        destination = "${NOMAD_TASK_DIR}/vars"
      }

      vault {
        policies = ["babbage"]
      }
    }
  }
}
