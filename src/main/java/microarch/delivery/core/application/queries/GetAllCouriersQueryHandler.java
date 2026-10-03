package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import java.util.List;

public interface GetAllCouriersQueryHandler {

    Result<List<GetAllCouriersResponse>, Error> handle();

}
