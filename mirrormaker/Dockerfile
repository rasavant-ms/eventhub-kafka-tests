#
# Copyright 2018 Confluent Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM confluentinc/cp-kafka:5.1.2

#ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

RUN bash -c 'cd /tmp && mkdir mirror_conf' 
COPY source-kafka.config /tmp/mirror_conf 
COPY mirror-eventhub.config /tmp/mirror_conf
COPY run.sh /tmp/mirror_conf
WORKDIR /tmp/mirror_conf

#RUN chmod +x run.sh

#CMD /tmp/mirror_conf/run.sh
