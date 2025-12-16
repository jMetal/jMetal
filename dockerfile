# Imagen base con JDK (ajusta la versión según tu proyecto)
FROM openjdk:22-jdk-slim

# Instalar Maven (si compilas desde código fuente de jMetal)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Copiar el proyecto (pom.xml + código fuente)
COPY . .

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Ejecutar un main específico (ajusta el nombre de la clase)
CMD ["java", "-cp", "jmetal-auto/target/jmetal-auto-6.9.4-SNAPSHOT-jar-with-dependencies.jar", "org.uma.jmetal.auto.autoconfigurablealgorithm.examples.NSGAIIConfiguredFromAParameterString"]

