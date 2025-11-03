Bu proje, bir çalışan yönetim sistemini simüle eden bir Spring Boot uygulamasıdır. Veritabanı işlemleri için MySQL kullanır, sorgu performansını artırmak için Redis ile önbelleğe alır ve çalışan resimlerini depolamak için Hadoop HDFS kullanır.

Proje Mimarisi
Web Sunucusu: Spring Boot (Embedded Tomcat)

Veritabanı (RDB): MySQL (Scott veritabanı)

Önbellek (Cache): Redis

Dosya Depolama: Hadoop HDFS (Tek Düğümlü)

Derleme Aracı: Gradle

1. Gereksinimler (Sistem Kurulumu)
Bu projenin çalışması için aşağıdaki servislerin bir WSL 2 (veya Linux) ortamında kurulu ve çalışır durumda olması gerekir:

Java 17 (openjdk-17-jdk)

MySQL Server (mysql-server)

Redis Server (redis-server)

Tek Düğümlü Hadoop Kümesi (HDFS)

Hadoop servisleri hadoop adında özel bir kullanıcı ile çalıştırılmalıdır.

Proje uygulamasını çalıştıran ana WSL kullanıcısının adı (örn: emrehalli) HDFS'te bir ev dizinine (/user/emrehalli) sahip olmalıdır.
