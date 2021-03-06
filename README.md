# Revolut test task notes

Хотел бы оставить пару замечаний, что можно было бы улучшить/изменить, но при определенных нюансах, разумеется.

Метод *onBaseCurrencyChanged* класса [CalculateAmountsUseCase](https://github.com/koflox/revolut_test/blob/master/app/src/main/java/com/koflox/revoluttest/use_cases/CalculateAmountsUseCase.kt):
1. Стоило бы использовать LinkedList для хранения валют, полученных с сервера (поле ratesResponse),
т.к. было бы удобно перемещать нововыбранную валюту в верх списка с минимальными затратами по времени и избавиться от создания нового списка в теле метода
2. Также можно не пересчитывать курсы валют вручную, а просто отправить новый запрос на сервер для обновления курса на основании новой валюты. При интервале запросов в 1 секунду это не сильно бы повлияло на трафик, но я считаю, что не стоит отправлять лишние запросы в любом случае.


Метод *run* класса [CalculateAmountsUseCase](https://github.com/koflox/revolut_test/blob/master/app/src/main/java/com/koflox/revoluttest/use_cases/CalculateAmountsUseCase.kt):

3. Если бы эта реализация была частью проекта в релизе, то стоило бы уточнить, что необходимо отображать пользователю, если с сервера прилетает список валют с валютой, которая отсутствовала в предыдущих ответах с сервера, особенно этот момент важен, если была выбрана новая основная валюта, т.к. считаю, что пользователь ожидает увидеть валюты в том порядке, который был до добавление новой валюты. Также стоит учесть, что необходимо сопостовлять порядок в новом ответе с порядком в предыдущем, т.к. сервер не всегда сохраняет порядок валют, например при запросе с [base=EUR](https://hiring.revolut.codes/api/android/latest?base=EUR), где первой валютой идет AUD, и последующем запросе с [base=AUD](https://hiring.revolut.codes/api/android/latest?base=AUD), первой валютой уже явлется BGN, а не EUR, как ожидалось.
Один из вариантов - это отображение новой валюты внизу списка, т.е.:

| Первый ответ   | Отображаем    | Выбрали USD  | Новый ответ | Remapping и отображем |
| :-------------:|:-------------:|:------------:|:-----------:|:---------------------:|
| EUR 1.00       | EUR 1.00      | USD 1.00     | USD 1.00    | USD 1.00              |
| RUB 2.00       | RUB 2.00      | EUR 0.25     | RUB 0.5     | EUR 0.25              |
| USD 4.00       | USD 4.00      | RUB 0.5      | IDR 0.05    | RUB 0.5               |
|                |               |              | EUR 0.25    | IDR 0.05              |


И второй вариант - игнорирование новых валют до нового открытия экрана (пересоздания). На данный момент используется второй вариант лишь из-за чуть более простой реализации

4. Также для улучшения производительности сопоставления/поиска валют после очередного ответа с сервера, стоило бы их хранить в HashMap'e, если бы нам не был важен порядок отображения при первом запросе.


5. На сервере присутсвует проблема с IDR валютой. При выборе данной валюты, как основной [base=IDR](https://hiring.revolut.codes/api/android/latest?base=IDR) большинство обменных курсов равны 0.0, что плохо сказывается на UX, т.к. в независимости от введеной величины, будь то 1_000_000 IDR или больше, она всегда будет равна 0 EUR и другим валютам с нулевым курсом. Реальный же курс примерно [1 INR == 0.000063 EUR](https://www.google.com/search?q=idr+to+eur)

6. Также я использовал эксперементальные фичи, чего не стоило бы делать в релизном приложении, но не очень хотелось строить взаимодействие с domain слоем на callback'ах.

7. Стоит проработать введение обозначений для чисел, превышающих видимую область в одну строку, например 1B, 1M и т.д
