package uk.gov.hmcts.ethos.scenario

object Feeders {

    val random = new scala.util.Random
    val repeat  = List(1, 2, 3,4,5)

    def sequenceValue() =
        Stream.continually(repeat.toStream).flatten.take(5).toList

    val DataFeeder = Iterator.continually(Map("service" -> ({
    "ECM"
    }),
        "SignoutNumber" -> ({
        "1000"
        })
    ));

}