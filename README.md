# Утилита для Автоматизации Развертывания в Yandex Cloud

Этот проект представляет собой консольную утилиту, разработанную на Java для автоматизации развертывания и настройки сетевой инфраструктуры в облаке Yandex Cloud. Утилита использует Yandex Cloud API для управления виртуальными машинами, создания NAT-инстансов, настройки маршрутизации и безопасности, а также для выполнения команд через SSH. 

## Используемые Технологии

- **Язык**: Java 21
- **Библиотеки**:
  - `java-sdk-services` для взаимодействия с Yandex Cloud API (создание ВМ, мониторинг).
  - `org.ini4j` для работы с `.ini` файлами.
  - `sshj` для установки SSH-соединений и удаленного выполнения команд на ВМ.

## Функциональность

1. Создание ВМ с заданными параметрами, включая настройку ядер, памяти, дисков, ОС и сетевых интерфейсов
2. Развертывание NAT-инстансов для доступа к интернету, настройка маршрутизации и перенаправления портов
3. Выполнения скриптов и команд на созданных ВМ для установки ПО и дополнительной настройки окружения

## Примеры Конфигурационных Файлов `.ini`

### 1. Создание NAT-инстанса
```ini
[GENERAL]
folder_id=b1gbssc7fd3bno967hd8 // либо создается новый каталог, тогда следует указывать folder_name 
zone_id=ru-central1-b
vm_count=2

[NAT_NETWORK]
name=my-vpc

[NAT_PRIVATE_SUBNET]
name=private-subnet
network_id=
id=
cidr_blocks=10.128.2.0/24

[NAT_PUBLIC_SUBNET]
name=public-subnet
network_id=
id=
cidr_blocks=10.128.1.0/24

[NAT_SECURITY_GROUP]
name=nat-instance-sg
rules=EGRESS any 0 65535 any 0.0.0.0/0,INGRESS ssh 22 22 tcp 0.0.0.0/0,INGRESS ext-http 80 80 tcp 0.0.0.0/0,INGRESS ext-https 443 443 tcp 0.0.0.0/0

[NAT_ROUTE_TABLE]
name=nat-instance-route
destination_prefix=0.0.0.0/0

[PORT_FORWARDING]
port_mapping_base=80:20000, 443:21000

[NAT_INSTANCE]
prefix=nat-instance-
platform_id=standard-v1
core=2
memory=2
disk_size=8
subnet_id=
image_id=fd8svplria89abvk0gfi
image_family=nat-instance-ubuntu
user_name=lena
path_public_ssh=C:/Users/User/.ssh/id_rsa.pub
path_private_ssh=C:/Users/User/.ssh/id_rsa

[VM]
prefix=vm-prefix-
platform_id=standard-v1
core=2
memory=4
disk_size=10
subnet_id=
image_standard=standard-images
image_family=ubuntu-1804
user_name=lena
path_public_ssh=C:/Users/User/.ssh/id_rsa.pub
path_private_ssh=```

### 2. Создание Виртуальных Машин с Публичными IP
```ini
[GENERAL]
folder_id=b1gbssc7fd3bno967hd8
zone_id=ru-central1-b
vm_count=2

[VM]
prefix=vm-prefix-
platform_id=standard-v1
core=2
memory=4
disk_size=10
subnet_id=
image_standard=standard-images
image_family=ubuntu-1804
user_name=lena
path_public_ssh=C:/Users/User/.ssh/id_rsa.pub
path_private_ssh=
commands_file_path=
```

### 3. Запуск Скрипта на Удаленной Машине
```ini
[RUN]
user_name=lena
host=130.193.42.133
ssh_private_path=C:/Users/User/.ssh/id_rsa
commands=src/main/resources/user_commands.txt
```

## Установка и Запуск

1. Склонируйте репозиторий.
2. Настройте файл конфигурации `.ini`.
3. Запустите приложение, указав путь к конфигурации:

   ```bash
   java -jar yourApp.jar path/to/config.ini
   ```

## Переменные Окружения

- `OAUTH_TOKEN`: Токен для аутентификации в Yandex Cloud API.
