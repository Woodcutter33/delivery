package microarch.delivery.adapters.in.shedulers;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.commands.AssignOrderCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignOrdersJob implements Job {

    private final AssignOrderCommandHandler commandHandler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        commandHandler.handle();
    }
}
