FROM maven:3.9.9-eclipse-temurin-21

# Crea uno spazio persistente per la m2
VOLUME ["/root/.m2"]

# Cartella di lavoro (mount del progetto)
WORKDIR /workspace

# Avvio di default: una shell
CMD ["/bin/bash"]