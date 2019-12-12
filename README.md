# peerToPeerUDP
Programa em Java que utiliza o protocolo UDP e um protocolo básico para comunicação P2P.\n

Observações gerais:\n
Chamarei de origem os programas rodados por "você" e de destino os quais "você" deseja receber/enviar arquivos.\n
Para suportar todas as requisições, são disparadas threads.\n
Somente o cliente dono do arquivo pode excluí-lo de toda a rede, excluindo em sua pasta.\n
A porta para comunicação definida é a 29000 e o diretório compartilhado deve ser escolhido nos parâmetros passados nas classes de teste/execução do programa.\n

Como o cliente funciona:\n
Para se manter atualizado, o cliente atualiza a cada determinado tempo. O padrão é 5 segundos.\n
Os IPs dos servidores conectados a rede devem ser passados por um arquivo "listaIPs.txt" que deve estar dentro da pasta do mesmo. A separação dos mesmos deve ser feita por "," (ignorar as aspas). Deve ser rodado a partir da classe "ExecClient".\n
Métodos:\n
- PTA - O cliente origem pede todos os arquivos ao servidor destino.
- PAE - O cliente origem recebe do servidor o nome do arquivo a ser pedido e o pede ao servidor destino.

Como o servidor funciona:\n
Métodos:\n
- PTA - O servidor origem envia uma lista com todos os seus arquivos disponíveis. 
- PAE - O servidor origem envia o arquivo pedido pelo cliente destino.
- ETA - O servidor origem recebe uma String com a lista dos arquivos do servidor destino ao qual o cliente origem havia pedido. Compara a lista recebida com os arquivos existentes, se detectar uma exclusão excluí, se detectar arquivos faltando pede através do cliente origem para o servidor destino.
- EAE - O servidor origem recebe o arquivo e o escreve no diretório.
