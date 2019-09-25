# IF710 2019.2 - Exercício 3 - Podcast Player

Esta tarefa envolve os conceitos de `Service`, `BroadcastReceiver`, `SystemServices` e princípios de UI. 
Siga os passos na ordem sugerida abaixo e identifique quais os passos completados no seu repositório. 

Este exercício é baseado no [exercício anterior](https://github.com/if710/2019.2-exercicio-2-podcast). Portanto, não há código base, pois você deve continuar a partir da sua resolução. 

7. Modifique a aplicação de forma que ao clicar em download, o arquivo mp3 do episódio seja baixado usando um `Service` (dica: use `IntentService`). Após o download ser completado, salve a localização do arquivo no banco de dados para facilitar o *play*;
8. Modifique o layout `itemlista.xml` para incluir um botão de *play* usando algum ícone ([ver opções aqui](https://www.iconfinder.com/search/?q=android%20play)). 
9. Ajuste o `Adapter`, de forma que, caso um episódio já esteja baixado, o botão de *play* seja habilitado para disparar um `Service` que toca o episódio - use um `foreground service` para poder gerenciar o play/pause de uma notificação;
10. Adicione uma `Preference` para escolher a frequência em que o *feed* de podcast será baixado novamente, via um `Service`. Use um `WorkManager` ([referência aqui](https://developer.android.com/topic/libraries/architecture/workmanager)) para agendar as tarefas. Inclua a possibilidade de alterar a `SharedPreference` por meio da `PreferenceScreen` já criada no exercício anterior;
11. Ao finalizar o download do episódio, o `Service` deve enviar um `broadcast` avisando que terminou, usando `LocalBroadcastManager`;
12. Use um `BroadcastReceiver` registrado dinamicamente, para que, quando o usuário estiver com o app em primeiro plano, a atualização do botão de *play* seja feita de forma automática;
13. Caso o usuário pause o episódio, salve no banco de dados a posição do áudio onde o episódio foi pausado, para que inicie do mesmo ponto da próxima vez que for dado o play;
14. Ao terminar de tocar um episódio, apague automaticamente o arquivo do armazenamento do dispositivo. 

---

# Orientações

  - Comente o código que você desenvolver, explicando o que cada parte faz.
  - Entregue o exercício *mesmo que não tenha completado todos os itens listados acima*. (marque no arquivo README.md do seu repositório o que completou, usando o template abaixo)

----

# Status

| Passo | Completou? |
| ------ | ------ |
| 7 | **não** |
| 8 | **não** |
| 9 | **não** |
| 10 | **não** |
| 11 | **não** |
| 12 | **não** |
| 13 | **não** |
| 14 | **não** |
