/*
 * This file is part of inject, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.inject;

import team.unnamed.inject.key.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
