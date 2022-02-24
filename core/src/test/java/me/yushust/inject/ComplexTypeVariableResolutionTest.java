package me.yushust.inject;

import me.yushust.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

// test for issue reported by pixeldev (github.com/pixeldev)
public class ComplexTypeVariableResolutionTest {

    @Test
    public void test() {
        Injector injector = Injector.create(binder -> {
            binder.bind(new TypeReference<RemoteModelService<DummyModel>>() {
                    })
                    .toInstance(new RemoteModelService<>());

            binder.bind(DummyService.class).to(SimpleDummyService.class).singleton();
        });

        DummyService dummyService = injector.getInstance(DummyService.class);
        Assertions.assertNotNull(dummyService);
        Assertions.assertNotNull(dummyService.get("test"));
    }

    interface Model {

        String getId();

    }

    public interface Service<T extends Model> {

        T get(String id);

    }

    public interface DummyService
            extends Service<DummyModel> {

    }

    public class DummyModel implements Model {

        @Override
        public String getId() {
            return "DUMMY";
        }

    }

    public class RemoteModelService<T extends Model> {

    }

    public abstract class AbstractService<T extends Model>
            implements Service<T> {

        @Inject
        protected RemoteModelService<T> modelService;

    }

    public class SimpleDummyService
            extends AbstractService<DummyModel>
            implements DummyService {

        @Inject
        public SimpleDummyService() {

        }

        @Override
        public DummyModel get(String id) {
            if (modelService == null) {
                return null;
            }

            return new DummyModel();
        }

    }

}
