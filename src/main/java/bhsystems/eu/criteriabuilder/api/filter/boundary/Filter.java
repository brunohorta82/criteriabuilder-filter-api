package bhsystems.eu.criteriabuilder.api.filter.boundary;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by brunohorta on 07/04/2017.
 */
public class Filter {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    public static Predicate[] parseToPredicate(String q, CriteriaBuilder cb, Root<?> member) throws InvalidSearchField {
        if (q == null) {
            return new Predicate[0];
        }
        final String[] parts = q.split(":");
        String[] fields = new String[0];
        String targetClass = null;
        if (parts.length > 0) {
            targetClass = parts[0];
        }
        System.out.println("TARGET CLASS: " + targetClass);
        if (parts.length > 1) {
            fields = parts[1].split(";");
        }
        try {
            List<Predicate> predicates = new ArrayList<>();
            for (String sq : fields) {
                String[] splitAnd = sq.split("\\|");
                for (String field : splitAnd) {
                    if (field.contains("=")) {
                        String[] splitEqual = field.split("=");
                        if (splitEqual.length == 2) {
                            predicates.add(cb.equal(member.get(splitEqual[0]), computeIntance(member.get(splitEqual[0]), splitEqual[1])));

                        }

                    }
                    if (field.contains("<")) {
                        String[] splitLess = field.split("<");
                        if (splitLess.length == 2) {
                            try {
                                predicates.add(cb.le(member.get(splitLess[0]), NumberFormat.getInstance().parse((String) computeIntance(member.get(splitLess[0]), splitLess[1]))));
                            } catch (ParseException e) {
                                throw new InvalidSearchField(e.getMessage());
                            }
                        }
                    }
                    if (field.contains(">")) {
                        String[] splitGreater = field.split(">");
                        if (splitGreater.length == 2) {
                            try {
                                predicates.add(cb.ge(member.get(splitGreater[0]), NumberFormat.getInstance().parse((String) computeIntance(member.get(splitGreater[0]), splitGreater[1]))));
                            } catch (ParseException e) {
                                throw new InvalidSearchField(e.getMessage());
                            }
                        }
                    }
                    if (field.contains("<>")) {
                        String[] splitNotEqual = field.split("<>");
                        if (splitNotEqual.length == 2) {
                            predicates.add(cb.notEqual(member.get(splitNotEqual[0]), computeIntance(member.get(splitNotEqual[0]), splitNotEqual[0])));
                        }
                    }
                }
            }

            return predicates.stream().toArray(Predicate[]::new);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw new InvalidSearchField(e, targetClass);
        }
    }

    public Object computeIntance(Path path, Object o) {
        if (path.getJavaType().isEnum()) {
            return Enum.valueOf(path.getJavaType(), o.toString().trim());
        }
        return o;
    }
}
