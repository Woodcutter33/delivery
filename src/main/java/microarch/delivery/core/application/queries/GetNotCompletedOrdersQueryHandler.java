package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import java.util.List;

public interface GetNotCompletedOrdersQueryHandler {

    Result<List<GetNotCompletedOrdersResponse>, Error> handle();

}
