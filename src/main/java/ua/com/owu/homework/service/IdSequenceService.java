package ua.com.owu.homework.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ua.com.owu.homework.model.DatabaseSequence;
import ua.com.owu.homework.repository.CarRepository;

import java.util.Objects;

@Service
public class IdSequenceService {
    private static final String CARS_SEQUENCE = "cars";

    private final MongoOperations mongoOperations;
    private final CarRepository carRepository;

    public IdSequenceService(MongoOperations mongoOperations, CarRepository carRepository) {
        this.mongoOperations = mongoOperations;
        this.carRepository = carRepository;
    }

    public long nextCarId() {
        long currentMaximum = carRepository.findAll().stream()
                .map(car -> car.getId())
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L);
        Query query = Query.query(Criteria.where("_id").is(CARS_SEQUENCE));

        // Initialize/raise the counter for existing data, then increment atomically.
        mongoOperations.upsert(query,
                new Update().setOnInsert("value", currentMaximum), DatabaseSequence.class);
        mongoOperations.updateFirst(query, new Update().max("value", currentMaximum), DatabaseSequence.class);
        DatabaseSequence sequence = mongoOperations.findAndModify(
                query, new Update().inc("value", 1),
                FindAndModifyOptions.options().returnNew(true), DatabaseSequence.class);
        return sequence.getValue();
    }
}
